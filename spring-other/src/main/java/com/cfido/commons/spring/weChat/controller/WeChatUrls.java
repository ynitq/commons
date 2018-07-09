package com.cfido.commons.spring.weChat.controller;

/** 各类url */
public interface WeChatUrls {
	/** 接收回调主机的url */
	public final static String PREFIX = "/wechatProxy";

	public final static String MASTER_CALL = "/masterCall";

	public final static String MASTER_CALLBACK = "/masterCallback";

	public final static String AGENT_CALLBACK = "/agentCallback";

}
