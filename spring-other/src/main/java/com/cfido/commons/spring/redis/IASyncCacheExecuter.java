package com.cfido.commons.spring.redis;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

/**
 * <pre>
 * IASyncCacheExecuter的接口，实际没啥用
 * </pre>
 * 
 * @author 梁韦江 2017年4月27日
 */
public interface IASyncCacheExecuter {

	/**
	 * 实现 {@link Cache#clear()}
	 * 
	 * @param cache
	 */
	void asyncClear(Cache cache);

	/**
	 * 实现 {@link Cache#evict(Object)}
	 * 
	 * @param cache
	 * @param key
	 */
	void asyncEvict(Cache cache, Object key);

	void asyncPut(Cache cache, Object key, Object value);

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
	ValueWrapper putIfAbsent(Cache cache, Object key, Object value);

	<T> T get(Cache cache, Object key, Class<T> type);

	/**
	 * 实现 {@link Cache#get(Object)}
	 * 
	 * @param cache
	 * @param key
	 * @return
	 */
	ValueWrapper get(Cache cache, Object key);

	/**
	 * 异步调用put之前，先将数据放到内存
	 * 
	 * @param cache
	 * @param key
	 * @param value
	 */
	void putToMemoryCache(Cache cache, Object key, Object value);

}
