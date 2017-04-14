package com.linzi.common.loginCheck;

/**
 * <pre>
 * 查询已经登录的WebUser的接口，主要是提供了已登录用户查询的功能
 * </pre>
 * 
 * @author 梁韦江
 *  2016年9月1日
 */
public interface IWebUserProvider {
	<T extends IWebUser> T getUser(Class<? extends IWebUser> clazz);
}
