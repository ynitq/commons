package com.cfido.commons.spring.jmxInWeb.models;

import javax.management.MBeanParameterInfo;

import com.cfido.commons.utils.utils.OpenTypeUtil;

/**
 * <pre>
 * 对MBeanParameterInfo的包装
 * </pre>
 * 
 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a>
 * 2015年10月15日
 */
public class MBeanOpParamVo {

	private final int id;
	private final MBeanParameterInfo info;
	private final boolean inputable;

	public MBeanOpParamVo(int id, MBeanParameterInfo info) {
		super();
		this.id = id;
		this.info = info;

		this.inputable = OpenTypeUtil.isOpenType(info.getType());
	}

	public MBeanParameterInfo getInfo() {
		return info;
	}

	public boolean isInputable() {
		return inputable;
	}

	public String getDefaultValue() {
		return OpenTypeUtil.getDefaultValue(this.info.getType());
	}

	public int getId() {
		return id;
	}
}
