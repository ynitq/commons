package com.cfido.commons.spring.apiServer.beans.ws;

import java.util.Date;

import com.cfido.commons.annotation.bean.AComment;

/**
 * <pre>
 * 对返回内容的封装
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public abstract class BasePushBean {

	/** 0: 调用方法的返回结果，-1: 调用方法时发生了错误, 1: 这个是个推送的事件, */
	@AComment("0: 调用方法的返回结果，-1: 调用方法时发生了错误, 1: 这个是个推送的事件,")
	private final int msgType;

	@AComment("响应生成的时间")
	private final Date createTime = new Date();

	public Date getCreateTime() {
		return createTime;
	}

	public BasePushBean(int msgType) {
		this.msgType = msgType;
	}

	public int getMsgType() {
		return msgType;
	}

}
