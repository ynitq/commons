package com.cfido.commons.spring.dict.inf.responses;

import com.cfido.commons.beans.apiServer.BaseResponse;

/**
 * <pre>
 * 当前用户的基础信息
 * </pre>
 * 
 * @author 梁韦江 2016年11月14日
 */
public class UserInfoResponse extends BaseResponse {

	/** 当前登陆的账号名 */
	private String account;

	/** 是否已经登录 */
	private boolean logined;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public boolean isLogined() {
		return logined;
	}

	public void setLogined(boolean logined) {
		this.logined = logined;
	}

}
