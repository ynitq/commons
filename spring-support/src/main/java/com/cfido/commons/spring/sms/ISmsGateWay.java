package com.cfido.commons.spring.sms;

/**
 * <pre>
 * 短信网关接口
 * </pre>
 * 
 * @author 梁韦江
 */
public interface ISmsGateWay {
	/**
	 * @param phone
	 *            手机号
	 * @param text
	 *            要发送的文本
	 * @param extno
	 *            可选参数，扩展码，用户定义扩展码，
	 * @throws SmsGateWayException
	 */
	void sendSms(String phone, String text, String extno) throws SmsGateWayException;
}
