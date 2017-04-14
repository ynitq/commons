package com.cfido.commons.beans.apiExceptions;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 发送短信时，发生了故障，例如发送次数超过限制了
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月3日
 */
public class SystemErrorWhenSendSmsException extends BaseApiException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getErrorMsg() {
		return "系统繁忙，请稍后再试";
	}

}
