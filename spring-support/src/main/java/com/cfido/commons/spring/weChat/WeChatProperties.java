package com.cfido.commons.spring.weChat;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import com.cfido.commons.utils.oauth.WeChatOAuthClient;

/**
 * <pre>
 * 微信 相关配置参数
 * 
 * wechat.appId = 
 * wechat.appSecret = 
 * wechat.jsAuth=true 激活JS安全域名认证
 * wechat.proxy=true 激活转发服务
 * 
 * wechat.porxyMap.testGetCode=http://test.rating.liangwj.com/wx/callback?state=testGetCode
 * wechat.porxyMap.testGetUserInfo=http://test.rating.liangwj.com/wx/callback?state=testGetUserInfo
 * 
 * 参数在 application.properties 中配置
 * </pre>
 * 
 * @author 梁韦江 2016年8月11日
 */
@ConfigurationProperties(prefix = "wechat")
public class WeChatProperties {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WeChatProperties.class);

	/** 公众号的唯一标识 */
	private String appId;

	/** 公众号的appsecret */
	private String appSecret;

	/** 额外的认证url，用于做微信网页认证的桥接 */
	private String authUrl;

	/** 配置微信回调转发服务的初始化 map */
	private Map<String, String> proxyMap;

	public Map<String, String> getProxyMap() {
		return proxyMap;
	}

	public void setProxyMap(Map<String, String> proxyMap) {
		this.proxyMap = proxyMap;
	}

	public String getAuthUrl() {
		return authUrl;
	}

	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}

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

		if (StringUtils.hasText(this.authUrl)) {
			log.info("微信认证配置了额外的url. wechat.authUrl={}", this.authUrl);
		}

		log.info("微信 AppId={}", this.appId);
	}

	public WeChatOAuthClient newClient() {
		WeChatOAuthClient s = new WeChatOAuthClient(this.appId, this.appSecret);
		return s;
	}

}
