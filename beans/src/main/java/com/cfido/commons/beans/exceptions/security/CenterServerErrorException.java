package com.cfido.commons.beans.exceptions.security;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 用户中心服务器访问错误
 * </pre>
 * 
 * @author 梁韦江
 */
public class CenterServerErrorException extends BaseApiException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getErrorMsg() {
		return "无此用户";
	}

}
