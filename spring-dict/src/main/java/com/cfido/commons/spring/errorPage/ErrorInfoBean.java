package com.cfido.commons.spring.errorPage;

import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.utils.utils.LogUtil;

/**
 * <pre>
 * 出错时，传到页面的错误信息
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月24日
 */
public class ErrorInfoBean {
	private final String requestUri;// 发生错误的uri
	private final int statusCode;// http状态码
	private final Throwable ex;// 错误类型
	private boolean debugMode;

	public ErrorInfoBean(String requestUri, int statusCode, Throwable ex) {
		this.requestUri = requestUri;
		this.statusCode = statusCode;
		this.ex = ex;
	}

	/**
	 * @return 发生错误时的uri
	 */
	public String getRequestUri() {
		return requestUri;
	}

	/**
	 * @return HTTP 状态码，例如：404、505、405
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @return 错误类
	 */
	public Throwable getException() {
		return ex;
	}

	/**
	 * @return 错误的class的名字
	 */
	public String getExceptionClassName() {
		if (this.ex != null) {
			return ex.getClass().getName();
		} else {
			return null;
		}
	}

	/**
	 * @return 错误消息
	 */
	public String getMessage() {
		if (this.ex != null) {
			return this.ex.getMessage();
		} else {
			return null;
		}
	}

	/**
	 * 是否是 api exception
	 * 
	 * @return
	 */
	public boolean isApiException() {
		return this.ex != null && this.ex instanceof BaseApiException;
	}

	/**
	 * @return 错误的StackTrace
	 */
	public String getStackTrace() {
		if (this.ex != null) {
			return LogUtil.getTraceString(null, ex);
		} else {
			return null;
		}
	}

	/**
	 * @return 是否可以显示 错误的调用过程
	 */
	public boolean isCanShowException() {
		return this.ex != null && this.isDebugMode() && this.statusCode == 500;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

}
