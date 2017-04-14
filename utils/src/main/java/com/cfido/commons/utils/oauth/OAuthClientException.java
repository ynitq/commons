package com.cfido.commons.utils.oauth;

import java.io.IOException;

import com.cfido.commons.utils.utils.HttpUtilException;

/**
 * <pre>
 * 包装 OAuthUtil发生的错误
 * </pre>
 * 
 * @author 梁韦江
 *  2016年7月19日
 */
public class OAuthClientException extends Exception {

	private static final long serialVersionUID = 1L;

	private final OAuthClientExceptionEnum type;

	public OAuthClientException(HttpUtilException ex) {
		super(ex);
		this.type = OAuthClientExceptionEnum.REST_CLIENT;
	}

	public OAuthClientException(OAuthClientExceptionEnum type) {
		super();
		this.type = type;
	}

	public OAuthClientException(IOException e) {
		super(e);
		this.type = OAuthClientExceptionEnum.IO_EXCEPTION;
	}

	@Override
	public String getMessage() {
		return this.type.message;
	}

}
