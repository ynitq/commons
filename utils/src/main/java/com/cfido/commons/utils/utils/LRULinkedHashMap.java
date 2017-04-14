package com.cfido.commons.utils.utils;

import java.util.LinkedHashMap;

/**
 * <pre>
 * 用LRU淘汰算法的Cache
 * </pre>
 * 
 * @author 梁韦江
 * 2015年5月15日
 * @param <K>
 * @param <V>
 */
public class LRULinkedHashMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;

	private final int maxCapacity;

	private static final float DEFAULT_LOAD_FACTOR = 0.75f;

	public LRULinkedHashMap(int maxCapacity) {
		super(maxCapacity, DEFAULT_LOAD_FACTOR, true);
		this.maxCapacity = maxCapacity;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > maxCapacity;
	}
}
