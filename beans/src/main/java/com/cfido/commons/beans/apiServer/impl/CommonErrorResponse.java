package com.cfido.commons.beans.apiServer.impl;

import java.util.Map;

import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.apiServer.BaseResponse;

/**
 * <pre>
 * 调用发生异常时，通用的返回结果
 * </pre>
 * 
 * @author 梁韦江 2016年6月28日
 */
public class CommonErrorResponse extends BaseResponse {

	/** BaseApiException 上额外的数据 */
	private Map<String, Object> exData;

	public CommonErrorResponse(BaseApiException ex) {
		this.setCode(ex.getClass().getName());
		this.setMessage(ex.getErrorMsg());
	}

	public CommonErrorResponse(Throwable ex, String errorMessage) {
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

	public Map<String, Object> getExData() {
		return exData;
	}

	public void setExData(Map<String, Object> apiData) {
		if (apiData == null || apiData.isEmpty()) {
			this.exData = null;
		} else {
			this.exData = apiData;
		}
	}
}
