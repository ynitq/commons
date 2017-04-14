package com.cfido.commons.beans.apiServer.impl;

import com.cfido.commons.beans.apiServer.ApiCommonCode;
import com.cfido.commons.beans.apiServer.BaseResponse;

/**
 * <pre>
 * 通用的成功的响应
 * </pre>
 * 
 * @author 梁韦江
 *  2016年6月28日
 */
public class CommonSuccessResponse extends BaseResponse {

	public final static CommonSuccessResponse DEFAULT = new CommonSuccessResponse();

	public CommonSuccessResponse() {
		this.setCode(ApiCommonCode.RESPONSE_OK);
	}

}
