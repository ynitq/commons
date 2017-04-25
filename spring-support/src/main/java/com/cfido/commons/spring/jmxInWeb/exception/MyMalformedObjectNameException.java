package com.cfido.commons.spring.jmxInWeb.exception;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 *  ObjectName 格式错误
 * </pre>
 * 
 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年10月16日
 */
public class MyMalformedObjectNameException extends BaseApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String nameStr;

	public MyMalformedObjectNameException(String nameStr) {
		super(nameStr);
		this.nameStr = nameStr;
	}

	@Override
	public String getErrorMsg() {
		return String.format("ObjectName格式错误: %s", this.nameStr);
	}

	public String getNameStr() {
		return nameStr;
	}

}
