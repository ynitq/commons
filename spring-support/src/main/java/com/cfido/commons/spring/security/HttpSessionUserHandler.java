package com.cfido.commons.spring.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.cfido.commons.loginCheck.IWebUser;

/**
 * <pre>
 * 将用户放到session中的处理器
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月23日
 */
@Component
public class HttpSessionUserHandler {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HttpSessionUserHandler.class);

	public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, IWebUser user) {
		log.debug("将用户 {} 的信息保存到Session中", user.getUsername());

		String sessionName = getSessionName(request, user.getClass());
		request.getSession().setAttribute(sessionName, user);
	}

	public void onLogout(HttpServletRequest request, HttpServletResponse response, Class<? extends IWebUser> clazz) {

		log.debug("从 Session 中移除 {} 类型用户的值 ", clazz.getSimpleName());

		// logout 时，从session中删除用户信息
		String sessionName = getSessionName(request, clazz);
		request.getSession().removeAttribute(sessionName);
	}

	public <T extends IWebUser> T getUser(HttpServletRequest request, Class<? extends IWebUser> clazz) {
		String sessionName = getSessionName(request, clazz);

		@SuppressWarnings("unchecked")
		T user = (T) request.getSession().getAttribute(sessionName);
		return user;
	}

	/**
	 * 根据用户类获得Session中的名字
	 * 
	 * @param clazz
	 * @return
	 */
	private String getSessionName(HttpServletRequest request, Class<? extends IWebUser> clazz) {

		String sessionName = "user_" + clazz.getName();
		return sessionName;
	}

}
