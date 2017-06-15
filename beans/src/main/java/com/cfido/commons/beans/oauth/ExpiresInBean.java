package com.cfido.commons.beans.oauth;

import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 用于描述有超时属性的bean, oauth协议中的返回信息很多有有超时的属性
 * </pre>
 * 
 * @author 梁韦江 2017年6月13日
 */
public class ExpiresInBean {
	/** 这个bean 的创建时间 */
	private long createTime = System.currentTimeMillis();

	/** 例如access_token 的超时时间，单位（秒）例如 7200 */
	private long expires_in;

	public long getExpires_in() {
		return expires_in;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public void setExpires_in(long expires_in) {
		this.expires_in = expires_in;
	}

	/** 是否超时了 */
	public boolean checkIsExpired() {
		final long now = System.currentTimeMillis();
		return now > this.createTime + TimeUnit.SECONDS.toMillis(this.expires_in);
	}

}
