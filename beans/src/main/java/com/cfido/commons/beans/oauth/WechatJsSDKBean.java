package com.cfido.commons.beans.oauth;

import java.util.UUID;

/**
 * <pre>
 * 调用微信jssdk时，需要用到的信息
 * </pre>
 * 
 * @author 梁韦江 2017年6月13日
 */
public class WechatJsSDKBean {

	/** 这里用的是秒 */
	private long timestamp = System.currentTimeMillis() / 1000;

	private String nonceStr = UUID.randomUUID().toString();

	private String appId;
	private String signature;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

}
