package com.cfido.commons.beans.appServer;

import com.cfido.commons.beans.apiServer.BaseResponse;

/**
 * 获取用户信息的结果，同于app登陆，也用于第三方用于获取用户信息
 */
public class UserResponse extends BaseResponse {

	/** 用户对象 */
	private OAuthUserInfo data;

	public OAuthUserInfo getData() {
		return data;
	}

	public void setData(OAuthUserInfo data) {
		this.data = data;
	}

}
