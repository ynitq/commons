package com.cfido.commons.beans.exceptions.security;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 登陆时，密码错误
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月3日
 */
public class InvalidPasswordException extends BaseApiException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getErrorMsg() {
		return "密码错误";
	}

}
