package com.cfido.commons.spring.weChat;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * <pre>
 * 微信 相关配置参数
 * 
 * wechat.appId = 
 * wechat.appSecret = 
 * wechat.jsAuth=true 激活JS安全域名认证
 * wechat.masterUrl=http://www.rating.liangwj.com
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

	/** 公众号后台设置的回调域名 例如 http://www.rating.liangwj.com */
	private String masterUrl;

	/** 错误页面的模板 */
	private String errorPage = "wechat/error";

	public String getMasterUrl() {
		return masterUrl;
	}

	public void setMasterUrl(String masterUrl) {
		this.masterUrl = masterUrl;
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

	public String getErrorPage() {
		return errorPage;
	}

	public void setErrorPage(String errorPage) {
		this.errorPage = errorPage;
	}

	@PostConstruct
	public void check() {

		if (StringUtils.isEmpty(appId)) {
			throw new RuntimeException("请配置 wechat.appId 参数");
		}
		if (StringUtils.isEmpty(appSecret)) {
			throw new RuntimeException("请配置 wechat.appSecret 参数");
		}

		log.info("微信 AppId={}", this.appId);
	}

	public WeChatOAuthClient newClient() {
		WeChatOAuthClient s = new WeChatOAuthClient(this.appId, this.appSecret);
		return s;
	}

}
