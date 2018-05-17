package com.cfido.commons.spring.security;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import com.cfido.commons.loginCheck.ANeedCheckLogin;
import com.cfido.commons.utils.utils.ClassUtil;
import com.cfido.commons.utils.utils.LogUtil;
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

	public static boolean isAjaxRequest(HttpServletRequest webRequest) {
		String requestedWith = webRequest.getHeader("X-Requested-With");
		return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
	}

	public static boolean isAjaxUploadRequest(HttpServletRequest webRequest) {
		return webRequest.getParameter("ajaxUpload") != null;
	}

	/**
	 * 通过拦截器传过来的参数，生成实例
	 * 
	 * @param request
	 * @param target
	 * @return
	 */
	protected static ActionInfo create(HttpServletRequest request, Object target, String defaultLoginUrl) {
		ActionInfo res = new ActionInfo();
		res.initDebugMsg(request);

		if (target instanceof org.springframework.web.method.HandlerMethod) {
			res.init(request, (HandlerMethod) target, defaultLoginUrl);
		} else {
			if (log.isDebugEnabled()) {
				res.debugMsg.append(" 其他Handler ").append(target.getClass().getSimpleName());
			}
		}
		return res;
	}

	private boolean isAjax = false;

	private ANeedCheckLogin loginCheck;

	/** 目标方法 */
	private Method targetMethod;

	private final long createTime = System.currentTimeMillis();

	private StringBuffer debugMsg;

	private String loginUrl;

	private ActionInfo() {
	}

	/** 返回方法上的注解 */
	public <A extends Annotation> A getAnnotationFromMethod(Class<A> annotationClass) {
		if (this.targetMethod == null) {
			return null;
		}
		return this.targetMethod.getAnnotation(annotationClass);
	}

	/** 返回方法上以及所属类上的注解 */
	public <A extends Annotation> A getAnnotationFromMethodAndClass(Class<A> annotationClass) {
		if (this.targetMethod == null) {
			return null;
		}
		return ClassUtil.getAnnotationFromMethodAndClass(this.targetMethod, annotationClass);
	}

	public ANeedCheckLogin getLoginCheck() {
		return loginCheck;
	}

	/**
	 * 是否声明了是ajax
	 * 
	 * @return
	 */
	public boolean isAjax() {
		return isAjax;
	}

	public boolean isHasTarget() {
		return this.targetMethod != null;
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
	private void init(HttpServletRequest request, HandlerMethod handlerMethod, String defaultLoginUrl) {

		Class<?> clazz = handlerMethod.getBean().getClass();
		targetMethod = handlerMethod.getMethod();

		// 先看看方法中是否有定义需要检查权限
		this.loginCheck = ClassUtil.getAnnotationFromMethodAndClass(targetMethod, ANeedCheckLogin.class);

		if (this.loginCheck != null) {
			// 有些检查 注解中的登录url，如果注解中没有什么，就用配置文件中
			if (StringUtils.isEmpty(this.loginCheck.loginUrl())) {
				this.loginUrl = defaultLoginUrl;
			} else {
				this.loginUrl = this.loginCheck.loginUrl();
			}
		}

		this.isAjax = isAjaxRequest(request);

		if (log.isDebugEnabled()) {

			debugMsg.append(String.format(" 方法: %s:%s()", clazz.getName(), targetMethod.getName()));

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
						loginCheck.userClass().getSimpleName()));
			}
		}
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

	protected void showDebugMsg() {
		if (log.isDebugEnabled() && this.debugMsg != null) {
			long time = System.currentTimeMillis() - this.createTime;
			this.debugMsg.append(" 花费时间:").append(time).append("ms");

			log.debug(this.debugMsg.toString());
		}
	}

}
