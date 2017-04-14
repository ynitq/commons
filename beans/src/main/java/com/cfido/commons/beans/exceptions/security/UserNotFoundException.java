package com.cfido.commons.beans.exceptions.security;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 用户找不到
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月3日
 */
public class UserNotFoundException extends BaseApiException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getErrorMsg() {
		return "无此用户";
	}

}
