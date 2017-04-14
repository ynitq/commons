package com.cfido.commons.beans.apiExceptions;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 缺少某字段
 * </pre>
 * 
 * @author 梁韦江
 *  2016年9月2日
 */
public class MissFieldException extends BaseApiException {

	private static final long serialVersionUID = 1L;

	private final String message;

	public MissFieldException(String message) {
		super();
		this.message = message;
	}

	@Override
	public String getErrorMsg() {
		return this.message;
	}

}
