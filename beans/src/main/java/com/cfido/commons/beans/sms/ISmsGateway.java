package com.cfido.commons.beans.sms;

import java.io.IOException;

import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.apiServer.BaseResponse;

/**
 * <pre>
 * 短信网关调用接口，其实目前只实现了一种
 * </pre>
 * 
 * @author 梁韦江 2016年7月20日
 */
public interface ISmsGateway<T extends BaseResponse> {
	/**
	 * 发送短信
	 * 
	 * @param form
	 *            包含了电话号码和发送内容的表单
	 *            
	 * @param removeIp 用户的ip            
	 * @return 返回 响应的对象
	 * 
	 * @throws BaseApiException
	 *             可能是io错误，也可能是短信网关那边返回的错误
	 * @throws IOException
	 *             IO错误
	 */
	T sendSmsMsg(SendSmsForm form, String removeIp) throws BaseApiException, IOException;
}
