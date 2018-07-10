package com.cfido.commons.spring.apiServer.beans.ws;

import org.springframework.util.Assert;

import com.cfido.commons.annotation.bean.AComment;

/**
 * <pre>
 * 调用接口成功时的返回
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public class ServerEventBean extends BasePushBean {

	@AComment("事件的数据")
	private final Object data;

	@AComment("事件的名字")
	private final String eventName;

	public ServerEventBean(Object event) {
		super(1);
		Assert.notNull(event, "要推送的事件不能 为空");

		this.eventName = event.getClass().getSimpleName();
		this.data = event;
	}

	public Object getData() {
		return data;
	}

	public String getEventName() {
		return eventName;
	}

}
