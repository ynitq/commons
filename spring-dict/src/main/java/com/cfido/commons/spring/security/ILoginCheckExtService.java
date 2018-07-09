package com.cfido.commons.spring.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <pre>
 * LoginCheck扩展的检查器
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public interface ILoginCheckExtService {

	/**
	 * 在检查权限之前执行
	 */
	public void beforeCheckRight(ActionInfo info, HttpServletResponse response, HttpServletRequest request)
			throws LoginExtCheckException;

	/**
	 * 在检查权限之后执行
	 */
	public void afterCheckRight(ActionInfo info, HttpServletResponse response, HttpServletRequest request)
			throws LoginExtCheckException;

}
