package com.cfido.commons.utils.utils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <pre>
 * 每个对象有时间限制的Map。 该map是线程安全的
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月24日
 */
public class TimeLimitMap<K extends Serializable, V> {

	private class ValueInCache {
		final long expireTime = System.currentTimeMillis() + TimeLimitMap.this.maxAge;
		final V value;

		public ValueInCache(V value) {
			super();
			this.value = value;
		}

		/**
		 * 是否在有效期内
		 * 
		 * @return
		 */
		boolean isValid() {
			return System.currentTimeMillis() < this.expireTime;
		}
	}

	private final Lock lock = new ReentrantLock();

	/** 缓存的map */
	private final LRULinkedHashMap<K, ValueInCache> map;

	/** 对象有效期 ms */
	private long maxAge;

	private final int maxCapacity;

	public TimeLimitMap(int maxCapacity) {
		this(5, TimeUnit.SECONDS, maxCapacity);
	}

	public TimeLimitMap(long time, TimeUnit timeUnit, int maxCapacity) {
		this.maxCapacity = maxCapacity;
		this.map = new LRULinkedHashMap<>(maxCapacity);

		this.maxAge = timeUnit.toMillis(time);
	}

	public void clear() {
		lock.lock();
		try {
			this.map.clear();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 清除所有失效的值
	 */
	public void clearInvalidValue() {
		lock.lock();
		try {
			List<Map.Entry<K, ValueInCache>> list = new LinkedList<>();
			list.addAll(this.map.entrySet());
			for (Map.Entry<K, ValueInCache> en : list) {
				if (!en.getValue().isValid()) {
					this.map.remove(en.getKey());
				}
			}
		} finally {
			lock.unlock();
		}
	}

	public V get(K key) {
		lock.lock();
		try {
			ValueInCache en = this.map.get(key);
			if (en != null) {
				if (en.isValid()) {
					return en.value;
				} else {
					this.map.remove(key);
				}
			}
			return null;

		} finally {
			lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(K key, Class<T> valueClass) {
		V value = this.get(key);
		if (value != null && valueClass.isAssignableFrom(value.getClass())) {
			return (T) value;
		}
		return null;
	}

	public long getMaxAge() {
		return maxAge;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void put(K key, V value) {
		if (key == null || value == null) {
			return;
		}

		lock.lock();
		try {
			this.map.put(key, new ValueInCache(value));
		} finally {
			lock.unlock();
		}
	}

	public void remove(K key) {
		lock.lock();
		try {
			this.map.remove(key);
		} finally {
			lock.unlock();
		}
	}

	public void setMaxAge(long maxAge) {
		if (maxAge > 500) {
			// 不能小于500毫秒
			this.maxAge = maxAge;
		}
	}

	public int size() {
		return this.map.size();
	}

}
