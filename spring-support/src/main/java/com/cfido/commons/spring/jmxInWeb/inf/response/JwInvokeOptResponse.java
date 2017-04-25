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
	private String returnData = "NULL";

	private String opName;

	public boolean isHasReturn() {
		return hasReturn;
	}

	public void setHasReturn(boolean hasReturn) {
		this.hasReturn = hasReturn;
	}

	public String getReturnData() {
		return returnData;
	}

	public void setReturnData(Object returnData) {
		if (returnData != null) {
			this.returnData = returnData.toString();
		} else {
			this.returnData = "null";
		}
	}

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

}
