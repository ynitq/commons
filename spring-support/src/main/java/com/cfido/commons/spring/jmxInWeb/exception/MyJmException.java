package com.cfido.commons.spring.jmxInWeb.exception;

import javax.management.JMException;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 调用jmx的过程发送了错误
 * </pre>
 * 
 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年10月15日
 */
public class MyJmException extends BaseApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JMException ex;

	public MyJmException(JMException ex) {
		super(ex);
		this.ex = ex;
	}

	public JMException getEx() {
		return ex;
	}

	@Override
	public String getErrorMsg() {
		return this.ex.getMessage();
	}

}
