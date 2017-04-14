package com.cfido.commons.beans.exceptions.security;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 用户被冻结
 * </pre>
 * 
 * @author 鲁炎
 *  2016年12月27日
 */
public class UserDisableException extends BaseApiException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getErrorMsg() {
		return "此用户已被冻结";
	}

}
