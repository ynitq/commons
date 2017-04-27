package com.cfido.commons.spring.jmxInWeb.models;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <pre>
 * 对Mbean进行分类显示用
 * </pre>
 * 
 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年9月11日
 */
public class DomainVo implements Comparable<DomainVo> {
	private final String name;
	private final List<MBeanVo> beans = new LinkedList<>();

	private boolean sorted = false;

	private int order = 0;

	public DomainVo(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<MBeanVo> getBeans() {
		if (!this.sorted) {
			this.sorted = true;
			Collections.sort(this.beans);
		}
		return beans;
	}

	public void addMBean(MBeanVo vo) {
		this.beans.add(vo);
	}

	@Override
	public int compareTo(DomainVo other) {
		int res = other.order - this.order;
		if (res == 0) {
			return this.name.compareTo(other.name);
		} else {
			return res;
		}
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

}
