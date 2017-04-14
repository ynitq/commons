package com.cfido.commons.spring.weChat;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.cfido.commons.utils.oauth.WeChatOAuthClient;
import com.cfido.commons.utils.utils.StringUtils;

/**
 * <pre>
 * 微信 相关配置参数
 * 
 * wechat.appId = 
 * wechat.appSecret = 
 * 
 * 参数在 application.properties 中配置
 * </pre>
 * 
 * @author 梁韦江 2016年8月11日
 */
@ConfigurationProperties(prefix = "wechat")
public class WeChatProperties {

	/** 公众号的唯一标识 */
	private String appId;

	/** 公众号的appsecret */
	private String appSecret;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	@PostConstruct
	public void check() {
		if (StringUtils.isEmpty(appId)) {
			throw new RuntimeException("请配置 wechat.appId 参数");
		}
		if (StringUtils.isEmpty(appSecret)) {
			throw new RuntimeException("请配置 wechat.appSecret 参数");
		}
	}

	public WeChatOAuthClient newClient() {
		WeChatOAuthClient s = new WeChatOAuthClient(this.appId, this.appSecret);
		return s;
	}

}
