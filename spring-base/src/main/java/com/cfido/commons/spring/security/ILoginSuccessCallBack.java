package com.cfido.commons.spring.security;

import com.cfido.commons.loginCheck.IWebUser;

/**
 * <pre>
 * 登录成功时的回调
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public interface ILoginSuccessCallBack {

	void onLoginSuccess(IWebUser user);

}
