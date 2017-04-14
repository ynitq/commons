package com.cfido.commons.codeGen.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * <pre>
 * 管理import的工具
 * </pre>
 * 
 * @author 梁韦江 2016年9月19日
 */
public class ImportClassSet {

	private final Set<String> classNameSet = new HashSet<>();

	/**
	 * 获得所有的import，并排序
	 */
	public List<String> getImportList() {
		List<String> list = new LinkedList<>();
		list.addAll(classNameSet);

		// 排序
		Collections.sort(list);

		return list;
	}

	/**
	 * 添加类，字符串形式
	 */
	public void add(String name) {
		if (name == null) {
			return;
		}

		if (!name.startsWith("java.lang.")) {
			this.classNameSet.add(name);
		}
	}

	public void add(Class<?> e) {
		this.add(e.getName());
	}

	public void addAll(Collection<? extends Class<?>> c) {
		if (c != null) {
			for (Class<?> clazz : c) {
				this.add(clazz);
			}
		}
	}

}
