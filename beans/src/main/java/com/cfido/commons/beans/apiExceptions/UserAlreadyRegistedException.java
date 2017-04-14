package com.cfido.commons.beans.apiExceptions;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 该账号已经注册了
 * </pre>
 * 
 * @author 梁韦江 2016年9月5日
 */
public class UserAlreadyRegistedException extends BaseApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getErrorMsg() {
		return "该账号已经注册了";
	}

}
