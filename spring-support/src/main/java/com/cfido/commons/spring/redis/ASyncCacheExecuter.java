package com.cfido.commons.spring.redis;

import java.util.concurrent.TimeUnit;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.naming.SelfNaming;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.Assert;

import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;
import com.cfido.commons.utils.utils.TimeLimitMap;

/**
 * <pre>
 * 异步执行Cache动作的执行者，这个因为有@Async注解方法的缘故，会变成代理类，无法变成mbean
 * 
 * TODO JedisPool 在高并发时，会出现死锁的问题，所以先在cache上加个同步，但这个明显会影响性能
 * </pre>
 * 
 * @author 梁韦江 2016年8月25日
 */
public class ASyncCacheExecuter implements IASyncCacheExecuter, SelfNaming {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ASyncCacheExecuter.class);

	/**
	 * 获得二级Cache的可以
	 * 
	 * @param cache
	 * @param key
	 * @return
	 */
	protected static String getLevel2CacheKey(Cache cache, Object key) {
		return String.format("%s_%s", cache.getName(), String.valueOf(key));
	}

	/** 二级 cache */
	private final TimeLimitMap<String, ValueWrapper> level2CacheForValueWrapper;

	@Autowired
	private ASyncCacheExecuterCounterMBean counterMBean;

	public ASyncCacheExecuter(long expireTimeInSec, int level2CacheSize) {
		this.level2CacheForValueWrapper = new TimeLimitMap<>(expireTimeInSec, TimeUnit.SECONDS, level2CacheSize);
	}

	@Override
	@Async
	public void asyncClear(Cache cache) {
		log.debug("Cache({}).clear()", cache.getName());

		synchronized (cache) {
			this.level2CacheForValueWrapper.clear();
			cache.clear();
		}
	}

	@Override
	@Async
	public void asyncEvict(Cache cache, Object key) {
		log.debug("Cache({}).asyncEvict(key={})", cache.getName(), key);

		synchronized (cache) {
			String key2 = getLevel2CacheKey(cache, key);
			this.level2CacheForValueWrapper.remove(key2);

			// 执行原来的方法
			cache.evict(key);
		}
	}

	@Override
	@Async
	public void asyncPut(Cache cache, Object key, Object value) {

		long start = System.currentTimeMillis();

		Assert.notNull(cache, "cache 不能为空");
		Assert.notNull(key, "key 不能为空");
		Assert.notNull(value, "value 不能为空");

		synchronized (cache) {
			// 先将数据放到redis
			cache.put(key, value);
		}

		/**
		 * 下面这段sleep的代码用于测试是否真的是异步执行，实践证明要异步执行的方法必须是public的
		 */
		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e) {
		// }

		long time = System.currentTimeMillis() - start;
		// 计数器+1
		this.countPut(time);
		log.debug("Cache({}).asyncPut(key={}), time={}ms", cache.getName(), key, time);
	}

	/**
	 * 实现 {@link Cache#get(Object)}
	 * 
	 * @param cache
	 * @param key
	 * @return
	 */
	@Override
	public ValueWrapper get(Cache cache, Object key) {
		Assert.notNull(cache, "cache 不能为空");
		Assert.notNull(key, "key 不能为空");

		long start = System.currentTimeMillis();

		String key2 = getLevel2CacheKey(cache, key);

		String type = "Redis";

		// 先从内容获得
		ValueWrapper res = this.level2CacheForValueWrapper.get(key2);
		if (this.isMemeoryMiss(res)) {
			// 如果内存中没有，再尝试redis
			try {
				res = cache.get(key);
			} catch (Exception e) {
				log.warn("在读取 Cache({}) key={} 时发生了错误，删除cache中的值", cache.getName(), key);
				cache.evict(key);
			}
			// 统计redis的命中率
			if (this.isRedisHit(res)) {
				// 如果redis中有数据，就将数据放到内存
				this.level2CacheForValueWrapper.put(key2, res);
			}
		} else {
			type = "内存";
		}

		long time = System.currentTimeMillis() - start;

		// 统计 get的时间和次数
		this.countGet(time);

		if (log.isDebugEnabled()) {
			if (res != null) {
				log.debug("Cache({}).get(key={}), 成功，耗时:{}ms, {}", cache.getName(), key, time, type);
			} else {
				log.debug("Cache({}).get(key={}), 失败，耗时:{}ms, {}", cache.getName(), key, time, type);
			}
		}

		return res;
	}

	/**
	 * 实现 {@link Cache#get(Object, Class)}
	 * 
	 * @param cache
	 * @param key
	 * @param type
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Cache cache, Object key, Class<T> type) {
		Assert.notNull(cache, "cache 不能为空");
		Assert.notNull(key, "key 不能为空");
		Assert.notNull(type, "type 不能为空");

		T res = null;

		ValueWrapper vm = this.get(cache, key);
		if (vm != null) {
			// 如果能get到数据
			Object obj = vm.get();
			if (obj != null && type.isAssignableFrom(obj.getClass())) {
				// 如果get到的数据符合类型，就返回
				res = (T) obj;
			}
		}

		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfido.commons.spring.redis.IASyncCacheExecuter#putIfAbsent(org.
	 * springframework.cache.Cache, java.lang.Object, java.lang.Object)
	 */
	@Override
	public ValueWrapper putIfAbsent(Cache cache, Object key, Object value) {
		Assert.notNull(cache, "cache 不能为空");
		Assert.notNull(key, "key 不能为空");
		Assert.notNull(value, "value 不能为空");

		// 计数器+1
		this.counterMBean.incrementPutIfAbsent();

		synchronized (cache) {
			// 先执行原来的方法
			ValueWrapper vw = cache.putIfAbsent(key, value);

			// 将结果放到内存中
			String key2 = getLevel2CacheKey(cache, key);
			this.level2CacheForValueWrapper.put(key2, vw);

			return vw;
		}
	}

	/**
	 * 异步调用put之前，先将数据放到内存
	 * 
	 * @param cache
	 * @param key
	 * @param value
	 */
	@Override
	public void putToMemoryCache(Cache cache, Object key, Object value) {
		String key2 = getLevel2CacheKey(cache, key);
		ValueWrapper vw = new SimpleValueWrapper(value);
		this.level2CacheForValueWrapper.put(key2, vw);
	}

	/**
	 * 判断内存Cache是否没有命中，顺便统计数据
	 * 
	 * @param obj
	 * @return
	 */
	private boolean isMemeoryMiss(Object obj) {
		if (obj == null) {
			this.counterMBean.incrementMemoryMiss();
			return true;
		} else {
			this.counterMBean.incrementMemoryHit();
			return false;
		}
	}

	/**
	 * 统计Redis的命中率
	 * 
	 * @param obj
	 */
	private boolean isRedisHit(Object obj) {
		if (obj == null) {
			this.counterMBean.incrementRedisMiss();
			return false;
		} else {
			this.counterMBean.incrementRedisHit();
			return true;
		}
	}

	/**
	 * 统计get的次数和时间
	 * 
	 * @param time
	 */
	private void countGet(long time) {
		this.counterMBean.countGet(time);
	}

	/**
	 * 统计get的次数和时间
	 * 
	 * @param time
	 */
	private void countPut(long time) {
		this.counterMBean.countPut(time);
	}

	@ManagedOperation(description = "清理内存Cache中已经过时的数据")
	public void clearInvalidMemeroyData() {
		this.level2CacheForValueWrapper.clearInvalidValue();
	}

	@ManagedOperation(description = "清理内存Cache中所有数据")
	public void clearAllMemeoryData() {
		this.level2CacheForValueWrapper.clear();
	}

	@Override
	public ObjectName getObjectName() throws MalformedObjectNameException {
		String name = String.format("%s:name=%s", CommonMBeanDomainNaming.DOMAIN, "ASyncCacheExecuter");
		return new ObjectName(name);
	}
}
