package com.cfido.commons.spring.security;

import org.springframework.util.StringUtils;

import com.cfido.commons.beans.apiExceptions.InvalidLoginStatusException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.exceptions.security.PermissionDeniedException;
import com.cfido.commons.loginCheck.ANeedCheckLogin;
import com.cfido.commons.loginCheck.IWebUser;

/** 管理登录的上下文 */
public abstract class BaseLoginContext {

	/** 根据用户类型，获取用户对象 */
	public abstract <T extends IWebUser> T getUser(Class<T> clazz);

	/** 登录成功时，记录登录状态 */
	public abstract void onLoginSuccess(IWebUser user);

	/** 退出登录 */
	public abstract void onLogout(Class<? extends IWebUser> userClass);

	/**
	 * 根据方法的注解，检查权限
	 * 
	 * @param loginCheck
	 * @throws BaseApiException
	 */
	public void checkRight(ANeedCheckLogin loginCheck) throws InvalidLoginStatusException {
		if (loginCheck == null) {
			// 如果没有权限要求，就返回
			return;
		}

		IWebUser user = this.getUser(loginCheck.userClass());
		if (user == null) {
			// 如果没找到登录用户就抛错
			throw new InvalidLoginStatusException();
		}

		if (StringUtils.isEmpty(loginCheck.optId())) {
			// 如果没有指定需要特殊检查的权限id，就直接通过
			return;
		}

		if (user.checkRights(loginCheck.optId())) {
			// 只要有其中一个用户能通过权限校验，就当通过了
			return;
		}

		// 如果所有用户的权限都无法满足当前权限,就抛错
		throw new PermissionDeniedException();
	}
}
