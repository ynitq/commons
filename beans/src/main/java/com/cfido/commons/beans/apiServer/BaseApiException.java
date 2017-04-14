package com.cfido.commons.beans.apiServer;

/**
 * api抛错的基类，必须有错误代码和错误信息
 * 
 * @author liangwj
 * 
 */
public abstract class BaseApiException extends Exception {
	private static final long serialVersionUID = 1L;

	public BaseApiException() {
		super();
	}

	public BaseApiException(String paramString, Throwable paramThrowable, boolean paramBoolean1, boolean paramBoolean2) {
		super(paramString, paramThrowable, paramBoolean1, paramBoolean2);
	}

	public BaseApiException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public BaseApiException(String paramString) {
		super(paramString);
	}

	public BaseApiException(Throwable paramThrowable) {
		super(paramThrowable);
	}

	/**
	 * 错误的提示消息
	 * 
	 * @return 错误提示
	 */
	public abstract String getErrorMsg();

	@Override
	public String getMessage() {
		return this.getErrorMsg();
	}

	/**
	 * 获得返回码， 逻辑错误还是正常的返回200代码
	 * 
	 * @return
	 */
	public int getHttpStatusCode() {
		return 200;
	}

}
