package com.cfido.commons.beans.monitor;

import javax.validation.constraints.NotNull;

import com.cfido.commons.annotation.api.AForm;

/**
 * <pre>
 * 发送数据给服务器端的表单
 * </pre>
 * 
 * @author 梁韦江 2016年12月19日
 */
@AForm
public class ClientGetUserForm extends BaseClientSignForm {

	@NotNull
	private String account;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
}
