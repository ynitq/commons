package com.cfido.commons.beans.others;

/**
 * <pre>
 * 验证码服务用bean，用于保存到redis中
 * </pre>
 * 
 * @author 梁韦江 2017年5月25日
 */
public class CodeVerifyBean {
	private String code;
	private long time = System.currentTimeMillis();

	public CodeVerifyBean(String code) {
		this.code = code;
	}

	public CodeVerifyBean() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
