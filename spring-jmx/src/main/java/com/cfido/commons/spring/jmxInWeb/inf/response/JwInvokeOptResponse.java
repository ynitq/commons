package com.cfido.commons.spring.jmxInWeb.inf.response;

import com.cfido.commons.beans.apiServer.BaseResponse;

/**
 * <pre>
 * Invoke opt response
 * </pre>
 * 
 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年10月16日
 */
public class JwInvokeOptResponse extends BaseResponse {

	private boolean hasReturn;
	private String returnData;

	public boolean isHasReturn() {
		return hasReturn;
	}

	public void setHasReturn(boolean hasReturn) {
		this.hasReturn = hasReturn;
	}

	public String getReturnData() {
		return returnData;
	}

	public void setReturnData(String returnData) {
		this.returnData = returnData;
	}

}
