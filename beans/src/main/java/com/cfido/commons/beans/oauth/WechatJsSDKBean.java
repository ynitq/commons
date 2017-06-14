package com.cfido.commons.beans.oauth;

import java.util.UUID;

/**
 * <pre>
 * 调用微信jssdk时，需要用到的信息
 * 
 * 文档见 https://mp.weixin.qq.com/wiki
 * </pre>
 * 
 * @author 梁韦江 2017年6月13日
 */
public class WechatJsSDKBean {

	/** 时间戳，用的是秒 */
	private final long timestamp = System.currentTimeMillis() / 1000;

	/** 随机字符串 */
	private final String nonceStr = UUID.randomUUID().toString();

	private final String appId;
	private String signature;

	public WechatJsSDKBean(String appId) {
		super();
		this.appId = appId;
	}

	public String getAppId() {
		return appId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

}
