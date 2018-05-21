package com.cfido.commons.beans.oauth;

import com.cfido.commons.beans.exceptions.WeChatApiException;

/**
 * <pre>
 * 微信接口返回结果的基类，主要是判断是否有错误信息
 * </pre>
 * 
 * @author 梁韦江 2017年6月15日
 */
abstract class BaseWechatBean {
	/** 调用微信接口时，如果有错，这里就是错误代码 0:请求成功 */
	private Integer errcode;

	/** 调用微信接口时，如果有错，这里就是错误信息 */
	private String errmsg;

	public Integer getErrcode() {
		return errcode;
	}

	public void setErrcode(Integer errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public void checkIsSuccess() throws WeChatApiException {
		if (this.errcode != null) { // && this.errcode > 0
			throw new WeChatApiException(errcode, errmsg);
		}
	}

}
