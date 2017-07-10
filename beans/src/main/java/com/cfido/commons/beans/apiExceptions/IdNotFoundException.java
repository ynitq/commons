package com.cfido.commons.beans.apiExceptions;

import java.io.Serializable;

import com.cfido.commons.annotation.api.ADataInApiException;
import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 找不到数据
 * </pre>
 * 
 * @author 梁韦江 2016年7月2日
 */
public class IdNotFoundException extends BaseApiException {

	private static final long serialVersionUID = 1L;

	private final String errorMsg;

	@ADataInApiException
	private final Serializable id;

	public IdNotFoundException(String errorMsg, Serializable id) {
		super();
		this.errorMsg = errorMsg;
		this.id = id;
	}

	@Override
	public String getErrorMsg() {
		return this.errorMsg;
	}

	public Serializable getId() {
		return id;
	}

}
