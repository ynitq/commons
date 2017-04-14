package com.cfido.commons.beans.exceptions.security;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 修改密码时，旧密码错误
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月3日
 */
public class InvalidOldPasswordException extends BaseApiException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getErrorMsg() {
		return "旧密码错误";
	}

}
