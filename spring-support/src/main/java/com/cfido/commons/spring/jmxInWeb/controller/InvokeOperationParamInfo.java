package com.cfido.commons.spring.jmxInWeb.controller;

import javax.management.MBeanParameterInfo;

import com.cfido.commons.spring.jmxInWeb.exception.MyInvalidParamTypeException;
import com.cfido.commons.utils.utils.OpenTypeUtil;

/**
 * <pre>
 * 执行Mbean的一个操作时，对这个操作的参数的描述，以及各个参数的值
 * </pre>
 * 
 * @author 梁韦江 2017年4月25日
 */
public class InvokeOperationParamInfo {
	private final String[] types;
	private final Object[] values;

	public InvokeOperationParamInfo() {
		this.types = new String[0];
		this.values = new Object[0];
	}

	public InvokeOperationParamInfo(String[] types, String[] paramValue) throws MyInvalidParamTypeException {
		this.types = types;
		this.values = new Object[types.length];
		for (int i = 0; i < types.length; i++) {

			if (!OpenTypeUtil.isOpenType(types[i])) {
				throw new MyInvalidParamTypeException(types[i]);
			}

			values[i] = OpenTypeUtil.parserFromString(paramValue[i], types[i], "");
		}
	}

	public boolean isMath(MBeanParameterInfo[] params) {
		if (params.length != this.types.length) {
			return false;
		}

		for (int i = 0; i < params.length; i++) {
			if (!params[i].getType().equals(types[i])) {
				return false;
			}
		}
		return true;
	}

	public String[] getTypes() {
		return types;
	}

	public Object[] getValues() {
		return values;
	}

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();

		boolean first = true;
		for (String type : types) {
			if (!first) {
				buff.append(", ");
			} else {
				first = false;
			}
			buff.append(type);
		}

		return buff.toString();
	}

}