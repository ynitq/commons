package com.cfido.commons.beans.apiExceptions;

import com.cfido.commons.annotation.api.ADataInApiException;
import com.cfido.commons.beans.apiServer.BaseApiException;

/**
 * <pre>
 * 登陆状态失效了，需要重新授权
 * </pre>
 * 
 * @author 梁韦江 2016年7月2日
 */
public class ApiNotFoundException extends BaseApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ADataInApiException
	private final String url;

	public ApiNotFoundException(String infName, String methodName) {
		super();
		this.url = infName + "/" + methodName;
	}

	public ApiNotFoundException(String url) {
		super();
		this.url = url;
	}

	@Override
	public String getErrorMsg() {
		return "你所调用的访问的地址不存在";
	}

	public String getUrl() {
		return url;
	}

	@Override
	public int getHttpStatusCode() {
		return 404;
	}

}
