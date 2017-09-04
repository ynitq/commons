package com.cfido.commons.spring.sms;

import com.cfido.commons.annotation.api.ADataInApiException;
import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 发送量超过了每日限制
 * 
 * </pre>
 * 
 * @author 梁韦江 2016年7月21日
 */
public class SendSmsOutOfLimitException extends BaseApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2903989192722037043L;

	@ADataInApiException
	private final int dayLimit;

	public SendSmsOutOfLimitException(int dayLimit) {
		super();
		this.dayLimit = dayLimit;
	}

	public int getDayLimit() {
		return dayLimit;
	}

	@Override
	public String getErrorMsg() {
		return String.format("当天发送短信的数量已经超过了 %d 条的限制", dayLimit);
	}

}
