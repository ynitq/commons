package com.cfido.commons.beans.form;

import javax.validation.constraints.NotNull;

import com.cfido.commons.annotation.api.AForm;
import com.cfido.commons.annotation.api.AMock;
import com.cfido.commons.annotation.bean.AComment;

/**
 * <pre>
 * 登录用得表单
 * </pre>
 * 
 * @author 梁韦江 2016年9月8日
 */
@AForm
public class LoginForm {

	@AComment(value = "账号")
	@NotNull(message = "请输入账号")
	@AMock("admin")
	private String account;

	@AComment(value = "密码")
	@NotNull(message = "请输入密码")
	@AMock("linzi777")
	private String password;

	@AComment(value = "是否记住密码")
	private boolean rememberMe;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
