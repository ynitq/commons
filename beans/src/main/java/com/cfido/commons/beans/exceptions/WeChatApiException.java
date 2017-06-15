package com.cfido.commons.beans.exceptions;

import com.cfido.commons.annotation.api.ADataInApiException;
import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 调用微信接口时，发送了错误
 * </pre>
 * 
 * @author 梁韦江 2017年6月15日
 */
public class WeChatApiException extends BaseApiException {

	private static final long serialVersionUID = 1;

	@ADataInApiException
	private final int errcode;
	@ADataInApiException
	private final String errmsg;

	public WeChatApiException(int errcode, String errmsg) {
		super();
		this.errcode = errcode;
		this.errmsg = errmsg;
	}

	@Override
	public String getErrorMsg() {
		return String.format("调用微信接口时发生错误，错误代码:%d, 错误信息:%s", this.errcode, this.errmsg);
	}

	public int getErrcode() {
		return errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

}
