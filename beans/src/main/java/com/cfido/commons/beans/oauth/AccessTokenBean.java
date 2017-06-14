package com.cfido.commons.beans.oauth;

/**
 * <pre>
 * 用于存储access_token的bean，
 * 
 * 遵循 oauth 2.0 规范， 每一个access_token都会有超时的时间
 * 
 * 规范中的变量名实有下划线的，所以我们的变量也只能带下划线
 * 
 * </pre>
 * 
 * @author 梁韦江
 */
public class AccessTokenBean extends ExpiresInBean {

	/** access_token */
	private String access_token;

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

}
