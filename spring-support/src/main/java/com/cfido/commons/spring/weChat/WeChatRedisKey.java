package com.cfido.commons.spring.weChat;

/** 各类redis的key */
public interface WeChatRedisKey {

	public final static String PREFIX = "wechat";

	/** 保存api调用的access token */
	public final static String KEY_ACCESS_TOKEN = PREFIX + "accessToken";

	/** 保存 js sdk 需要用的 ticket */
	public final static String KEY_JSAPI = PREFIX + "jsApi";

	/** 保存 请求master前的当前的url */
	public final static String KEY_AGENT_PREFIX = PREFIX + "agentUrlMap:";

	/** 保存 微信回调转发时，agent传过来的key的前缀 */
	public final static String KEY_MASTER_PREFIX = PREFIX + "masterKeyMap:";
}
