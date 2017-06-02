package com.cfido.commons.beans.apiExceptions;

import com.cfido.commons.annotation.api.ADataInApiException;
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

	/** 剩余的秒数, 需要返回给客户端 */
	@ADataInApiException
	private final long remainInSec;

	public TooBusyWhenSendMailException(long remainInSec) {
		super();
		this.remainInSec = remainInSec;
	}

	public long getRemainInSec() {
		return remainInSec;
	}

	@Override
	public String getErrorMsg() {
		return "需要间隔" + this.remainInSec + "秒才能再次发送邮件验证码";
	}

}
