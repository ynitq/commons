package com.cfido.commons.utils.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <pre>
 * 用LRU淘汰算法的Cache
 * </pre>
 * 
 * @author 梁韦江 2015年5月15日
 * @param <K>
 * @param <V>
 */
public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;

	private final int maxCapacity;

	private static final float DEFAULT_LOAD_FACTOR = 0.75f;

	private final Lock lock = new ReentrantLock();

	public LRULinkedHashMap(int maxCapacity) {
		super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
		this.maxCapacity = maxCapacity;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > maxCapacity;
	}

	@Override
	public V get(Object key) {
		this.lock.lock();
		try {
			return super.get(key);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		this.lock.lock();
		try {
			return super.getOrDefault(key, defaultValue);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public V put(K key, V value) {
		this.lock.lock();
		try {
			return super.put(key, value);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		this.lock.lock();
		try {
			super.putAll(m);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public V remove(Object key) {
		this.lock.lock();
		try {
			return super.remove(key);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public boolean remove(Object key, Object value) {
		this.lock.lock();
		try {
			return super.remove(key, value);
		} finally {
			this.lock.unlock();
		}
	}

}
