package com.cfido.commons.spring.security;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.method.HandlerMethod;

import com.cfido.commons.annotation.other.AMonitorIngore;
import com.cfido.commons.beans.apiExceptions.InvalidLoginStatusException;
import com.cfido.commons.beans.exceptions.security.PermissionDeniedException;
import com.cfido.commons.loginCheck.ANeedCheckLogin;
import com.cfido.commons.loginCheck.IWebUser;
import com.cfido.commons.utils.utils.ClassUtil;
import com.cfido.commons.utils.utils.LogUtil;
import com.cfido.commons.utils.utils.StringUtils;
import com.cfido.commons.utils.web.WebUtils;

/**
 * <pre>
 * 用于分析Action中通过注解方式定义的属性，用于配合 ANeedCheckLogin
 * </pre>
 * 
 * @see ANeedCheckLogin 权限定义
 * 
 * @author 梁韦江 2015年7月14日
 */
public class ActionInfo {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ActionInfo.class);

	/**
	 * 要检查的在http session中的用户类的类型
	 */
	private final Map<Class<? extends IWebUser>, IWebUser> userMap = new HashMap<>(4);

	private boolean isAjax = false;

	private ANeedCheckLogin loginCheck;

	private final long createTime = System.currentTimeMillis();

	private StringBuffer debugMsg;

	private String loginUrl;

	/** 是否需要统计访问次数给监控系统 */
	private boolean needMonitorRequest = false;

	private ActionInfo() {

	}

	protected String getLoginUrl() {
		return this.loginUrl;
	}

	/**
	 * 该方法是否需要检查登录状态
	 * 
	 * @return
	 */
	protected boolean isNeedCheckLogin() {
		return this.loginCheck != null;
	}

	/**
	 * 是否声明了是ajax
	 * 
	 * @return
	 */
	public boolean isAjax() {
		return isAjax;
	}

	/**
	 * 检查登录状态
	 * 
	 * @throws InvalidLoginStatusException
	 */
	protected void checkLogin() throws InvalidLoginStatusException {
		if (!this.isNeedCheckLogin()) {
			// 如果根本不需要检查权限，则直接通过检查
			return;
		}

		if (this.userMap.isEmpty()) {
			// 如果没找到登录用户就抛错
			throw new InvalidLoginStatusException();
		}

		if (StringUtils.isEmpty(this.loginCheck.optId())) {
			// 如果没有指定需要特殊检查的权限id，就直接通过
			return;
		}

		// 遍历所有的用户，检查指定要特殊检查的权限id
		for (IWebUser user : this.userMap.values()) {
			if (user.checkRights(this.loginCheck.optId())) {
				// 只要有其中一个用户能通过权限校验，就当通过了
				return;
			}
		}

		// 如果所有用户的权限都无法满足当前权限,就抛错
		throw new PermissionDeniedException();

	}

	/**
	 * 通过拦截器传过来的参数，初始化
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param handlerMethod
	 *            HandlerMethod
	 * @param context
	 *            LoginContext
	 */
	private void init(HttpServletRequest request, HandlerMethod handlerMethod, LoginContext context) {

		Class<?> clazz = handlerMethod.getBean().getClass();
		Method method = handlerMethod.getMethod();

		// 先看看方法中是否有定义需要检查权限
		this.loginCheck = method.getAnnotation(ANeedCheckLogin.class);
		if (this.loginCheck == null) {
			// 如果方法中没有定义，则检查类是否有定义
			this.loginCheck = method.getDeclaringClass().getAnnotation(ANeedCheckLogin.class);
		}
		if (this.loginCheck != null) {
			// 如果该方法需要检查登录，则将从session中将注解中声明的所有用户类型获取出来
			for (Class<? extends IWebUser> userClass : this.loginCheck.userClass()) {
				IWebUser user = context.getUser(userClass);
				if (user != null) {
					this.userMap.put(userClass, user);
				}
			}

			// 有些检查 注解中的登录url，如果注解中没有什么，就用配置文件中
			if (StringUtils.isEmpty(this.loginCheck.loginUrl())) {
				this.loginUrl = context.getProp().getLoginUrl();
			} else {
				this.loginUrl = this.loginCheck.loginUrl();
			}
		}

		this.isAjax = isAjaxRequest(request);

		if (log.isDebugEnabled()) {

			debugMsg.append(String.format(" 方法: %s:%s()", clazz.getName(), method.getName()));

			if (!request.getParameterMap().isEmpty()) {
				debugMsg.append(" 参数:");
				WebUtils.debugRequest(request, debugMsg);
			}

			debugMsg.append("\n");
			if (this.isAjax) {
				debugMsg.append(" ajax请求");
			} else {
				debugMsg.append(" 页面请求");
			}

			if (loginCheck != null) {
				debugMsg.append(LogUtil.format(" 需要安全验证: %s ",
						loginCheck.userClass()[0].getSimpleName()));
			}
		}

		// 如果没有特殊的注解，该方法都需要被统计并汇报给监控系统
		AMonitorIngore monitorIngore = ClassUtil.getAnnotationFromMethodAndClass(method, AMonitorIngore.class);
		this.needMonitorRequest = (monitorIngore == null);

	}

	private void initDebugMsg(HttpServletRequest request) {
		if (log.isDebugEnabled()) {
			this.debugMsg = new StringBuffer(200);

			debugMsg.append(WebUtils.findRealRemoteAddr(request));

			debugMsg.append(" ");
			debugMsg.append(request.getMethod());
			debugMsg.append(" ");
			debugMsg.append(request.getRequestURI());

			String qs = request.getQueryString();
			if (qs != null) {
				debugMsg.append("?");
				debugMsg.append(qs);
			}
		}
	}

	protected void showDebugMsg() {
		if (log.isDebugEnabled() && this.debugMsg != null) {
			long time = System.currentTimeMillis() - this.createTime;
			this.debugMsg.append(" 花费时间:").append(time).append("ms");

			log.debug(this.debugMsg.toString());
		}
	}

	/**
	 * 通过拦截器传过来的参数，生成实例
	 * 
	 * @param request
	 * @param target
	 * @return
	 */
	protected static ActionInfo create(HttpServletRequest request, Object target, LoginContext context) {
		ActionInfo res = new ActionInfo();
		res.initDebugMsg(request);

		if (target instanceof org.springframework.web.method.HandlerMethod) {
			res.init(request, (HandlerMethod) target, context);
		} else {
			if (log.isDebugEnabled()) {
				res.debugMsg.append(" 其他Handler ").append(target.getClass().getSimpleName());
			}
		}
		return res;
	}

	public static boolean isAjaxRequest(HttpServletRequest webRequest) {
		String requestedWith = webRequest.getHeader("X-Requested-With");
		return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
	}

	public static boolean isAjaxUploadRequest(HttpServletRequest webRequest) {
		return webRequest.getParameter("ajaxUpload") != null;
	}

	public boolean isNeedMonitorRequest() {
		return needMonitorRequest;
	}

}
