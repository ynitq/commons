package com.cfido.commons.spring.sms;

/**
 * <pre>
 * 短信网关出现错误
 * </pre>
 * 
 * @author 梁韦江
 */
public class SmsGateWayException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SmsGateWayException() {
		super();
	}

	public SmsGateWayException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SmsGateWayException(String message, Throwable cause) {
		super(message, cause);
	}

	public SmsGateWayException(String message) {
		super(message);
	}

	public SmsGateWayException(Throwable cause) {
		super(cause);
	}

}
