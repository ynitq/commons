package com.cfido.commons.utils.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * 就是一个以INT为key的HashMap，
 * 重新包装的目的是：
 * HashMap[Integer, Object] 在做get等操作时，如果将key错写成String或其他类型，编译时无法发现错误
 * </pre>
 * 
 * @author 梁韦江 2015年5月15日
 * @param <V>
 */
public class IntKeyHashMap<V> {

	private final Map<Integer, V> map;

	public IntKeyHashMap() {
		this.map = new HashMap<Integer, V>();
	}

	public IntKeyHashMap(int size) {
		this.map = new HashMap<Integer, V>(size);
	}
	
	
	public V get(int key) {
		return map.get(key);
	}

	public V put(int key, V value) {
		return map.put(key, value);
	}

	public V remove(int key) {
		return map.remove(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(int key) {
		return map.containsKey(key);
	}

	public Set<java.util.Map.Entry<Integer, V>> entrySet() {
		return map.entrySet();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<Integer> keySet() {
		return map.keySet();
	}

	public void putAll(Map<? extends Integer, ? extends V> m) {
		map.putAll(m);
	}

	public int size() {
		return map.size();
	}

	public Collection<V> values() {
		return map.values();
	}
}
