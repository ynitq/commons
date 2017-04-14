package com.cfido.commons.beans.apiServer.impl;

import com.cfido.commons.beans.apiServer.ApiCommonCode;
import com.cfido.commons.beans.apiServer.BaseResponse;

/**
 * <pre>
 * 用于web页面的成功响应，主要是可以返回重定向的url
 * </pre>
 * 
 * @author 梁韦江
 *  2016年6月28日
 */
public class PageSuccessResponse extends BaseResponse {

	/** 成功后，重定向的url */
	private String redirectUrl;

	public PageSuccessResponse() {
		this.setCode(ApiCommonCode.RESPONSE_OK);
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

}
