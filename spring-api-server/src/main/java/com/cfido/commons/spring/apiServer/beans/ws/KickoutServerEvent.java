package com.cfido.commons.spring.apiServer.beans.ws;

import org.springframework.util.Assert;

import com.cfido.commons.annotation.bean.AComment;

/**
 * <pre>
 * 用户被踢下线的事件
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public class KickoutServerEvent extends BasePushBean {

	@AComment("被踢下去的原因")
	private final String data;

	@AComment("事件的名字")
	private final String eventName;

	public KickoutServerEvent(String event) {
		super(1);
		Assert.notNull(event, "踢人的原因不能为空");

		this.eventName = event.getClass().getSimpleName();
		this.data = event;
	}

	public String getData() {
		return data;
	}

	public String getEventName() {
		return eventName;
	}

}
