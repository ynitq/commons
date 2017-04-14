package com.cfido.commons.utils.oauth;

/**
 * <pre>
 * 错误类型的枚举
 * </pre>
 * 
 * @author 梁韦江
 *  2016年7月19日
 */
public enum OAuthClientExceptionEnum {
	REST_CLIENT("HTTP错误"),
	MISS_TOKEN("缺少token"),
	IO_EXCEPTION("IO错误")
	;

	public String message;

	private OAuthClientExceptionEnum(String message) {
		this.message = message;
	}

}
