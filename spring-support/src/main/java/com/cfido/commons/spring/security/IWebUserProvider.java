package com.cfido.commons.spring.security;

import com.cfido.commons.loginCheck.IWebUser;

/**
 * <pre>
 * 用户查询接口，为的是 rememeber me， 需要根据cookie中的值重新获取这个用户对象
 * </pre>
 * 
 * @author 梁韦江 2016年8月23日
 */
public interface IWebUserProvider<T extends IWebUser> {

	/**
	 * 需要声明这个类支持那些WebUser类
	 */
	Class<T> getSupportUserClassNames();

	/**
	 * 根据用户名获得用户
	 * 
	 * @param account
	 *            账号
	 * @return 如果有用户，就返回用户，没有就返回null
	 */
	T loadUserByAccount(String account);
}
