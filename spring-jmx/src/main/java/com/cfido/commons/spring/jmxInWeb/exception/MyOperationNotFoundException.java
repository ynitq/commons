package com.cfido.commons.spring.jmxInWeb.exception;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 无法根据optName找到对应的MBean中的操作
 * </pre>
 * 
 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年10月16日
 */
public class MyOperationNotFoundException extends BaseApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String operationsInfo;

	public MyOperationNotFoundException(String operationsInfo) {
		super(operationsInfo);
		this.operationsInfo = operationsInfo;
	}

	@Override
	public String getErrorMsg() {

		return String.format("Operation %s not found", this.operationsInfo);
	}

}
