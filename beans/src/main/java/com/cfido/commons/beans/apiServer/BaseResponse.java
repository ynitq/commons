package com.cfido.commons.beans.apiServer;

import com.cfido.commons.annotation.bean.AComment;

/**
 * 响应返回结果基类
 * 
 * 2016年5月10日 下午2:01:17
 */
public abstract class BaseResponse {

	public final static String DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 返回结果，成功是不需要判断，错误时这里是错误代码
	 */
	@AComment(value = "不成功的时候是Exception的类名")
	private String code = ApiCommonCode.RESPONSE_OK;

	/**
	 * 开发时，调试用
	 */
	@AComment(value = "调试模式才有的信息")
	private String debugMsg;

	/**
	 * 必须有 message这个属性在基类，否则android 客户端没法用
	 */
	@AComment(value = "一般是错误信息，正常情况下是空")
	private String message;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 是否成功, SHH那边的ok标志是 1 ， 真是要吐血了
	 * 
	 * @return success
	 */
	@AComment(value = "是否调用成功")
	public boolean isSuccess() {
		return ApiCommonCode.RESPONSE_OK.equals(code) || "1".equals(code);
	}

	public String getDebugMsg() {
		return debugMsg;
	}

	public void setDebugMsg(String debugMsg) {
		this.debugMsg = debugMsg;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
