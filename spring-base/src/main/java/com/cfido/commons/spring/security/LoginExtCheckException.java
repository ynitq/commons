package com.cfido.commons.spring.security;

/**
 * <pre>
 * ILoginCheckExtService 检查时，如果需要跳转，就抛这个错误
 * </pre>
 * 
 * @author 梁韦江
 * 
 * @see ILoginCheckExtService
 */
public class LoginExtCheckException extends Exception {

	private String redirectUrl;
	private boolean needRedirect;

	private static final long serialVersionUID = 1L;

	private LoginExtCheckException() {
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public boolean isNeedRedirect() {
		return needRedirect;
	}

	/** 创建需要重定向的错误 */
	public static LoginExtCheckException redirect(String redirectUrl) {
		LoginExtCheckException ex = new LoginExtCheckException();
		ex.redirectUrl = redirectUrl;
		ex.needRedirect = true;
		return ex;
	}

}
