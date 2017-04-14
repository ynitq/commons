package com.cfido.commons.beans.exceptions.security;

import com.cfido.commons.beans.apiExceptions.InvalidLoginStatusException;

/**
 * <pre>
 * 无此权限，继承于 InvalidLoginStatusException
 * </pre>
 * 
 * @see InvalidLoginStatusException
 * 
 * @author 梁韦江
 *  2016年7月2日
 */
public class PermissionDeniedException extends InvalidLoginStatusException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getErrorMsg() {
		return "对比起，你没有权限进行该操作，请重新登陆";
	}

}
