package com.cfido.commons.spring.weChat;

import org.springframework.util.StringUtils;

import com.cfido.commons.spring.weChat.controller.WeChatUrls;

/**
 * <pre>
 * 用于在master方，保存发起方的信息
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public class KeyBeanInMaster {

	private String agentHost;
	private String key;

	public String getAgentHost() {
		return agentHost;
	}

	public void setAgentHost(String agentHost) {
		this.agentHost = agentHost;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	/** 生成回调的url，同时将授权码和原来传过来的key加上 */
	public String toCallBackUrl(String code) {
		// 例子：http://xxx.com/wechatProxy/agentCallback?code=xx?key=xx
		StringBuilder sb = new StringBuilder();
		sb.append(this.agentHost);
		sb.append(WeChatUrls.PREFIX).append(WeChatUrls.AGENT_CALLBACK);
		sb.append('?');
		sb.append("code=").append(code);
		if (StringUtils.hasText(this.key)) {
			sb.append("&key=").append(this.key);
		}
		return sb.toString();
	}
}
