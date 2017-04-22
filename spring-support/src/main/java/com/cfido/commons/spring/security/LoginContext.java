package com.cfido.commons.spring.security;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cfido.commons.beans.apiExceptions.InvalidLoginStatusException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.exceptions.security.PermissionDeniedException;
import com.cfido.commons.loginCheck.ANeedCheckLogin;
import com.cfido.commons.loginCheck.IWebUser;
import com.cfido.commons.spring.utils.WebContextHolderHelper;
import com.cfido.commons.utils.utils.StringUtils;
import com.cfido.commons.utils.web.WebUtils;

/**
 * <pre>
 * 和登录服务相关的Context
 * </pre>
 * 
 * @author 梁韦江 2016年8月23日
 */
@Component
public class LoginContext {
	private static final String NEXT_PAGE_MSG_SESSION_NAME = "NEXT_PAGE_MSG_SESSION_NAME";

	/**
	 * 从一页传过来的信息的VO的名字，通常在_js.jsp中处理
	 */
	public static final String VO_NAME_MSG_FROM_PREV_PAGE = "msgFromPrevPage";

	/** 存储 ActionInfo */
	private final ThreadLocal<ActionInfo> threadLocalActionInfo = new ThreadLocal<>();

	@Autowired
	private HttpSessionUserHandler httpSessionUserHandler;

	@Autowired
	private RememberMeUserHandler remeberMeUserHandler;

	@Autowired
	private LoginCheckProperties prop;

	public LoginCheckProperties getProp() {
		return prop;
	}

	public void setProp(LoginCheckProperties prop) {
		this.prop = prop;
	}

	/**
	 * 获取用户
	 * 
	 * @param clazz
	 * @return
	 */
	public <T extends IWebUser> T getUser(Class<? extends IWebUser> clazz) {
		HttpServletRequest request = WebContextHolderHelper.getRequest();
		HttpServletResponse response = WebContextHolderHelper.getResponse();

		T user = this.httpSessionUserHandler.getUser(request, clazz);
		if (user == null) {
			// 如果session中找不到用户，就尝试看看是否有remeber me 的cookie
			user = this.remeberMeUserHandler.getUser(request, response, clazz);
			if (user != null) {
				// 如果获取用户成功，就放到session中
				this.httpSessionUserHandler.onLoginSuccess(request, response, user);
			}
		}

		return user;
	}

	/**
	 * 用户logout
	 * 
	 * @param userClass
	 */
	public void onLogout(Class<? extends IWebUser> userClass) {
		HttpServletRequest request = WebContextHolderHelper.getRequest();
		HttpServletResponse response = WebContextHolderHelper.getResponse();

		this.httpSessionUserHandler.onLogout(request, response, userClass);
		this.remeberMeUserHandler.onLogout(request, response, userClass);
	}

	/**
	 * 当用户登录时，保存用户状态
	 * 
	 * @param user
	 */
	public void onLoginSuccess(IWebUser user, boolean remeberMe) {
		if (user == null) {
			return;
		}

		HttpServletRequest request = WebContextHolderHelper.getRequest();
		HttpServletResponse response = WebContextHolderHelper.getResponse();

		this.httpSessionUserHandler.onLoginSuccess(request, response, user);

		if (this.isNeedRememberMe() || remeberMe) {
			this.remeberMeUserHandler.onLoginSuccess(request, response, user);
		}
	}

	/**
	 * 当用户登录时，保存用户状态
	 * 
	 * @param user
	 */
	public void onLoginSuccess(IWebUser user) {
		this.onLoginSuccess(user, false);
	}

	/**
	 * 是否需要记录用户登录状态到cookie
	 */
	private boolean isNeedRememberMe() {

		if (this.prop.getRememberMe().isAllwaysRememmberMe()) {
			// 如果设置了总是记录
			return true;
		}

		HttpServletRequest request = WebContextHolderHelper.getRequest();
		Map<String, String[]> map = request.getParameterMap();

		if (map.containsKey("remember-me")) {
			// 兼容就系统，原来的参数名中是减号
			return true;
		}
		if (map.containsKey(this.prop.getRememberMe().getParamName())) {
			// 如果request有remeber的参数
			return true;
		}

		return false;
	}

	public void saveActionInfo(ActionInfo info) {
		this.threadLocalActionInfo.set(info);
	}

	public ActionInfo getActionInfo() {
		return this.threadLocalActionInfo.get();
	}

	/**
	 * 获得全路径
	 * 
	 * @param path
	 * @return
	 */
	public String getFullPath(String path) {
		if (path == null) {
			return null;
		}
		HttpServletRequest request = WebContextHolderHelper.getRequest();

		String basePath = String.format("%s://%s:%d%s/%s",
				request.getScheme(),
				request.getServerName(),
				request.getServerPort(),
				request.getContextPath(),
				path);
		return basePath;
	}

	/**
	 * 发送信息给下一页
	 * 
	 * @param msg
	 */
	public void sendMsgToNextPage(String msg) {
		// 其实就是简单的保存到session而已，该保存的值，会在getHeader时中取回出来
		HttpServletRequest request = WebContextHolderHelper.getRequest();
		request.getSession().setAttribute(NEXT_PAGE_MSG_SESSION_NAME, msg);
	}

	/**
	 * 更新从上一页传过来的session信息
	 * 
	 * @param request
	 */
	protected void addMsgFromPrevPage(HttpServletRequest request) {
		// 如果有上一页过来的信息，就取出来，
		String msg = (String) request.getSession().getAttribute(NEXT_PAGE_MSG_SESSION_NAME);
		// 然后就从session中删除，防止再次读取
		request.getSession().removeAttribute(NEXT_PAGE_MSG_SESSION_NAME);

		// 将VO放到model中，
		request.setAttribute(VO_NAME_MSG_FROM_PREV_PAGE, msg);
	}

	/**
	 * 获得用户的ip
	 * 
	 * @return
	 */
	public String getRemoteIp() {
		HttpServletRequest request = WebContextHolderHelper.getRequest();
		return WebUtils.findRealRemoteAddr(request);
	}

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
