package com.cfido.commons.beans.apiExceptions;

import java.io.Serializable;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 找不到数据
 * </pre>
 * 
 * @author 梁韦江
 *  2016年7月2日
 */
public class IdNotFoundException extends BaseApiException {

	private static final long serialVersionUID = 1L;

	private final String objName;

	private final Serializable id;

	public IdNotFoundException(String objName, Serializable id) {
		super();
		this.objName = objName;
		this.id = id;
	}

	@Override
	public String getErrorMsg() {
		return String.format("找不到 id=%s 的 %s 数据 ", String.valueOf(this.id), this.objName);
	}

}
