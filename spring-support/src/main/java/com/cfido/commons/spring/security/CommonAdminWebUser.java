package com.cfido.commons.spring.security;

import com.cfido.commons.loginCheck.IWebUser;

/**
 * <pre>
 * 通用的系统管理用户，目前用于字典项目和jmx项目
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
public class CommonAdminWebUser implements IWebUser {

	private final String username;
	private final String password;

	private boolean superUser;

	public CommonAdminWebUser(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public boolean isSuperUser() {
		return superUser;
	}

	public void setSuperUser(boolean superUser) {
		this.superUser = superUser;
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
