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
		ActionInfo res = new ActionInfo(request);

		if (target instanceof org.springframework.web.method.HandlerMethod) {
			// 如果是动态请求
			res.init((HandlerMethod) target, defaultLoginUrl);
		}
		return res;
	}

	private boolean isAjax = false;

	private ANeedCheckLogin loginCheck;

	/** 目标方法 */
	private Method targetMethod;

	private final long createTime = System.currentTimeMillis();

	private String loginUrl;

	private final HttpServletRequest request;

	private ActionInfo(HttpServletRequest request) {
		this.request = request;
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
	private void init(HandlerMethod handlerMethod, String defaultLoginUrl) {

		// Class<?> targetClass = handlerMethod.getBean().getClass();
		this.targetMethod = handlerMethod.getMethod();

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
	}

	/** 获取调试信息 */
	private String getDebugInfo() {
		StringBuffer debugMsg = new StringBuffer(200);

		// 添加ip
		debugMsg.append(WebUtils.findRealRemoteAddr(request));

		// 添加请求方式
		debugMsg.append(" ");
		debugMsg.append(request.getMethod());

		// 添加url
		debugMsg.append(" ");
		debugMsg.append(request.getRequestURI());

		// 添加?后的参数
		String qs = request.getQueryString();
		if (qs != null) {
			debugMsg.append("?");
			debugMsg.append(qs);
		}

		// 添加方法名和类名
		Class<?> targetClass = this.targetMethod.getDeclaringClass();
		debugMsg.append(String.format("\n\t方法: %s:%s()", targetClass.getName(), targetMethod.getName()));

		if (!request.getParameterMap().isEmpty()) {
			debugMsg.append("\n\t参数:");
			WebUtils.debugRequest(request, debugMsg);
		}

		debugMsg.append("\n\t");
		if (this.isAjax) {
			debugMsg.append("ajax请求");
		} else {
			debugMsg.append("页面请求");
		}

		if (loginCheck != null) {
			debugMsg.append(LogUtil.format(" 需要安全验证: %s ",
					loginCheck.userClass().getSimpleName()));
		}

		return debugMsg.toString();
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
		long time = System.currentTimeMillis() - this.createTime;

		if (log.isDebugEnabled()) {
			log.debug("{}\n\t花费时间:{}ms", this.getDebugInfo(), time);
		}

		// 如果时间太长，需要警告
		if (time > 1000) {
			log.warn("{}\n\t执行时间过长，花费时间:{}ms", this.getDebugInfo(), time);
		}
	}

}
