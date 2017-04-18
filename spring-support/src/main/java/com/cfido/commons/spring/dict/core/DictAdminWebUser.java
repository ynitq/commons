package com.cfido.commons.spring.dict.core;

import com.cfido.commons.loginCheck.IWebUser;

/**
 * <pre>
 * 字典后台的管理用户
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
public class DictAdminWebUser implements IWebUser {

	private final String username;
	private final String password;

	public DictAdminWebUser(String username, String password) {
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
