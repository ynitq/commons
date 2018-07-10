package com.cfido.commons.spring.apiServer.beans.ws;

import com.cfido.commons.annotation.bean.AComment;

/**
 * <pre>
 * 对返回内容的封装
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public abstract class BaseResponseBean extends BasePushBean {

	@AComment("接口url")
	private final String url;

	@AComment("客户端传过来的序列号")
	private final String sid;

	public BaseResponseBean(int msgType, CmdBean cmd) {
		super(msgType);
		this.url = cmd.getUrl();
		this.sid = cmd.getSid();
	}

	public String getUrl() {
		return url;
	}

	public String getSid() {
		return sid;
	}

}
