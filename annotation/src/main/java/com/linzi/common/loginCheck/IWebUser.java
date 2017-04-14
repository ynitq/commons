package com.linzi.common.loginCheck;

/**
 * web项目中，要从session检查的用户对象的基类
 * 
 * @author liangwj
 * 
 */
public interface IWebUser {

	/**
	 * 用于检查权限
	 * 
	 * @param optId
	 * @return
	 */
	boolean checkRights(String optId);

	/**
	 * 返回用户名
	 * 
	 * @return
	 */
	String getUsername();

	/**
	 * 返回用于生成签名的密码，不需要是明文
	 * 
	 * @return
	 */
	String getPassword();

}
