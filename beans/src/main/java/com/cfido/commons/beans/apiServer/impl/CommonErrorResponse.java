package com.cfido.commons.beans.apiServer.impl;

import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.apiServer.BaseResponse;

/**
 * <pre>
 * 调用发生异常时，通用的返回结果
 * </pre>
 * 
 * @author 梁韦江
 *  2016年6月28日
 */
public class CommonErrorResponse extends BaseResponse {

	public CommonErrorResponse(BaseApiException ex) {
		this.setCode(ex.getClass().getName());
		this.setMessage(ex.getErrorMsg());
	}

	public CommonErrorResponse(Exception ex) {
		this.setCode(ex.getClass().getName());
		this.setMessage(ex.getMessage());
	}

	public CommonErrorResponse(Exception ex, String errorMessage) {
		this.setCode(ex.getClass().getName());
		this.setMessage(errorMessage);
	}

	public CommonErrorResponse(String errorMessage) {
		this();
		this.setMessage(errorMessage);
	}

	public CommonErrorResponse() {
		this.setCode("ERROR");
	}

	public String getErrorMsg() {
		return this.getMessage();
	}

	public void setErrorMsg(String errorMessage) {
		this.setMessage(errorMessage);
	}
}
