package com.cfido.commons.beans.apiExceptions;

import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 登陆状态失效了，需要重新授权
 * </pre>
 * 
 * @author 梁韦江
 *  2016年7月2日
 */
public class ApiNotFoundException extends BaseApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String infName;

	private final String methodName;
	
	public ApiNotFoundException(String infName, String methodName) {
		super();
		this.infName = infName;
		this.methodName = methodName;
	}

	@Override
	public String getErrorMsg() {
		return "你所调用的访问的地址不存在";
	}

	public String getInfName() {
		return infName;
	}

	public String getMethodName() {
		return methodName;
	}

	@Override
	public int getHttpStatusCode() {
		return 404;
	}

}
