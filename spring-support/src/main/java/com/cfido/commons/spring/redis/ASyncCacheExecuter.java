package com.cfido.commons.spring.redis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.cfido.commons.utils.utils.TimeLimitMap;

/**
 * <pre>
 * 异步执行Cache动作的执行者
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月25日
 */
@Service
@ManagedResource(description = "异步Cache执行器，命中率统计", objectName = "Common缓存:name=ASyncCacheExecuter")
public class ASyncCacheExecuter {

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

	private final AtomicLong counterForGet = new AtomicLong();
	private final AtomicLong counterForPutIfAbsent = new AtomicLong();
	private final AtomicLong counterForPut = new AtomicLong();
	private final AtomicLong memoryHit = new AtomicLong();
	private final AtomicLong memoryMiss = new AtomicLong();
	private final AtomicLong redisHit = new AtomicLong();
	private final AtomicLong redisMiss = new AtomicLong();
	private final AtomicLong timeForGet = new AtomicLong();
	private final AtomicLong timeForPut = new AtomicLong();

	public ASyncCacheExecuter(long expireTimeInSec, int level2CacheSize) {
		this.level2CacheForValueWrapper = new TimeLimitMap<>(expireTimeInSec, TimeUnit.SECONDS, level2CacheSize);
	}

	/**
	 * 实现 {@link Cache#clear()}
	 * 
	 * @param cache
	 */
	@Async
	public void asyncClear(Cache cache) {
		log.debug("Cache({}).clear()", cache.getName());

		this.level2CacheForValueWrapper.clear();
		cache.clear();
	}

	/**
	 * 实现 {@link Cache#evict(Object)}
	 * 
	 * @param cache
	 * @param key
	 */
	@Async
	public void asyncEvict(Cache cache, Object key) {
		log.debug("Cache({}).asyncEvict(key={})", cache.getName(), key);

		String key2 = getLevel2CacheKey(cache, key);
		this.level2CacheForValueWrapper.remove(key2);

		// 执行原来的方法
		cache.evict(key);
	}

	@Async
	public void asyncPut(Cache cache, Object key, Object value) {

		long start = System.currentTimeMillis();

		Assert.notNull(cache, "cache 不能为空");
		Assert.notNull(key, "key 不能为空");
		Assert.notNull(value, "value 不能为空");

		// 先将数据放到redis
		cache.put(key, value);

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
	ValueWrapper get(Cache cache, Object key) {
		Assert.notNull(cache, "cache 不能为空");
		Assert.notNull(key, "key 不能为空");

		long start = System.currentTimeMillis();

		String key2 = getLevel2CacheKey(cache, key);

		String type="Redis";
		
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
	@SuppressWarnings("unchecked")
	<T> T get(Cache cache, Object key, Class<T> type) {
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

	@ManagedAttribute(description = "get执行次数")
	public long getCountForGet() {
		return this.counterForGet.get();
	}

	@ManagedAttribute(description = "put执行次数")
	public long getCountForPut() {
		return this.counterForPut.get();
	}

	@ManagedAttribute(description = "平均每次get花费时间(毫秒)")
	public long getAvgTimeForGet() {
		long times = this.counterForGet.get();
		if (times == 0) {
			return 0;
		}

		long total = this.timeForGet.get();
		return total / times;
	}

	@ManagedAttribute(description = "平均每次put花费时间(毫秒)")
	public long getAvgTimeForPut() {
		long times = this.counterForPut.get();
		if (times == 0) {
			return 0;
		}

		long total = this.timeForPut.get();
		return total / times;
	}

	@ManagedAttribute(description = "内存Cache 命中次数")
	public long getMemoryHit() {
		return memoryHit.get();
	}

	@ManagedAttribute(description = "内存Cache miss次数")
	public long getMemoryMiss() {
		return this.memoryMiss.get();
	}

	@ManagedAttribute(description = "Redis Cache miss次数")
	public long getRedisMiss() {
		return this.redisMiss.get();
	}

	@ManagedAttribute(description = "Redis Cache 命中次数")
	public long getRedisHit() {
		return this.redisHit.get();
	}

	@ManagedAttribute(description = "内存 Cache 已使用的空间")
	public int getCacheCapacityUsed() {
		return this.level2CacheForValueWrapper.size();
	}

	@ManagedAttribute(description = "内存 Cache 超时时间（毫秒）")
	public long getCacheMaxAge() {
		return this.level2CacheForValueWrapper.getMaxAge();
	}

	@ManagedAttribute
	public void setCacheMaxAge(long maxAge) {
		this.level2CacheForValueWrapper.setMaxAge(maxAge);
	}

	@ManagedAttribute(description = "内存 Cache 可用空间总数")
	public int getCacheCapacityMax() {
		return this.level2CacheForValueWrapper.getMaxCapacity();
	}

	/**
	 * 实现了 {@link Cache#putIfAbsent(Object, Object)}
	 * 
	 * 这个方法需要返回数据，所以没法采用异步调用
	 * 
	 * @param cache
	 * @param key
	 * @param value
	 * @return
	 */
	public ValueWrapper putIfAbsent(Cache cache, Object key, Object value) {
		Assert.notNull(cache, "cache 不能为空");
		Assert.notNull(key, "key 不能为空");
		Assert.notNull(value, "value 不能为空");

		// 计数器+1
		this.counterForPutIfAbsent.incrementAndGet();

		// 先执行原来的方法
		ValueWrapper vw = cache.putIfAbsent(key, value);

		// 将结果放到内存中
		String key2 = getLevel2CacheKey(cache, key);
		this.level2CacheForValueWrapper.put(key2, vw);

		return vw;
	}

	/**
	 * 异步调用put之前，先将数据放到内存
	 * 
	 * @param cache
	 * @param key
	 * @param value
	 */
	void putToMemoryCache(Cache cache, Object key, Object value) {
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
			this.memoryMiss.incrementAndGet();
			return true;
		} else {
			this.memoryHit.incrementAndGet();
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
			this.redisMiss.incrementAndGet();
			return false;
		} else {
			this.redisHit.incrementAndGet();
			return true;
		}
	}

	/**
	 * 统计get的次数和时间
	 * 
	 * @param time
	 */
	private void countGet(long time) {
		this.counterForGet.incrementAndGet();
		this.timeForGet.addAndGet(time);
	}

	/**
	 * 统计get的次数和时间
	 * 
	 * @param time
	 */
	private void countPut(long time) {
		this.timeForPut.addAndGet(time);
		this.counterForPut.incrementAndGet();
	}

	@ManagedOperation(description = "重置所有的计数器")
	public void resetAllCounter() {

		log.debug("重置所有的计数器");

		this.counterForGet.set(0);
		this.counterForPut.set(0);
		this.counterForPutIfAbsent.set(0);
		this.memoryHit.set(0);
		this.memoryMiss.set(0);
		this.redisHit.set(0);
		this.redisMiss.set(0);
		this.timeForGet.set(0);
		this.timeForPut.set(0);
	}

	@ManagedOperation(description = "清理内存Cache中已经过时的数据")
	public void clearInvalidMemeroyData() {
		this.level2CacheForValueWrapper.clearInvalidValue();
	}

	@ManagedOperation(description = "清理内存Cache中所有数据")
	public void clearAllMemeoryData() {
		this.level2CacheForValueWrapper.clear();
	}
}
