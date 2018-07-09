package com.cfido.commons.spring.weChat;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 访问微信开发网关时出现了错误
 * </pre>
 * 
 * @author 梁韦江 2017年6月14日
 */
public class WeChatAccessFailException extends BaseApiException {

	private static final long serialVersionUID = 1L;

	public WeChatAccessFailException(String message) {
		super(message);
	}

	public WeChatAccessFailException(Throwable cause) {
		super(cause);
	}
}
