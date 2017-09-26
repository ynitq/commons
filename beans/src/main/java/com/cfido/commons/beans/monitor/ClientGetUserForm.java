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
public class ClientGetUserForm {

	/**
	 * 内容是 ClientIdBean的json字符串
	 * 
	 * @see ClientIdBean
	 */
	@NotNull
	private String idStr;

	@NotNull
	private String account;

	/** 时间 */
	private long createTime;

	/** 签名 */
	private String sign;

	public String getIdStr() {
		return idStr;
	}

	public void setIdStr(String idStr) {
		this.idStr = idStr;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
}
