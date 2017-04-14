package com.cfido.commons.beans.apiExceptions;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 其他错误
 * </pre>
 * 
 * @author 梁韦江
 *  2016年7月2日
 */
public class SystemErrorException extends BaseApiException {

	public SystemErrorException(Throwable paramThrowable) {
		super(paramThrowable);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getErrorMsg() {
		return "系统发生内部错误";
	}

}
