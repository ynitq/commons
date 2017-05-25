package com.cfido.commons.beans.apiExceptions;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 需要间隔1分钟才能再次发送邮件验证码
 * </pre>
 * 
 * @author 梁韦江 2016年8月3日
 */
public class TooBusyWhenSendMailException extends BaseApiException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getErrorMsg() {
		return "需要间隔1分钟才能再次发送邮件验证码";
	}

}
