package com.cfido.commons.beans.apiExceptions;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 验证码错误
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月3日
 */
public class InvalidSmsRandCodeException extends BaseApiException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getErrorMsg() {
		return "验证码错误";
	}

}
