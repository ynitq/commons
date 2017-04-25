package com.cfido.commons.spring.jmxInWeb.core;

import com.cfido.commons.loginCheck.IWebUser;

/**
 * <pre>
 * JmxInWeb的管理用户
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
public class JwWebUser implements IWebUser {

	private final String username;
	private final String password;

	public JwWebUser(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	@Override
	public boolean checkRights(String optId) {
		return true;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public String getPassword() {
		return this.password;
	}
}
