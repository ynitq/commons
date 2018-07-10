package com.cfido.commons.spring.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.apiServer.impl.CommonErrorResponse;
import com.cfido.commons.loginCheck.ANeedCheckLogin;
import com.cfido.commons.spring.debugMode.DebugModeProperties;
import com.cfido.commons.utils.utils.ExceptionUtil;
import com.cfido.commons.utils.web.WebUtils;

/**
 * 认证安全拦截器
 * 
 * <pre>
 * 1. 检查调用的方法是否需要登录，未登录的会重定向到登录页面
 * 2. 如果有remember-me参数，就记录密码
 * 
 * </pre>
 * 
 * @see LoginCheckProperties.RememberMe RememberMe参数
 * @see IWebUserProvider RememberMe需要根据用户名查询用户，所以需要实现该接口的一个方法
 * @see LoginContext#onLoginSuccess(com.linzi.common.others.IWebUser)
 *      登录成功后，要调用该方法保存用户
 * @see LoginContext#onLogout(Class) logout时，要调用该方法，清理session和cookie
 * 
 * @author 梁韦江
 * 
 */
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoginCheckInterceptor.class);

	@Autowired
	private LoginContext context;

	@Autowired
	private DebugModeProperties debugModeProperties;

	@Autowired
	private ApplicationContext applicationContext;

	private final List<ILoginCheckExtService> extCheckServiceList = new LinkedList<>();

	private final Map<String, Object> commonModel = new HashMap<>();

	public LoginCheckInterceptor() {
	}

	/**
	 * 增加其他的共用属性，增加后，每一个页面都会获得这些内容
	 * 
	 * @param attrName
	 *            属性名
	 * @param attrValue
	 *            属性值
	 */
	public void addCommonModel(String attrName, Object attrValue) {
		Assert.hasText(attrName, "attrName不能为空");
		Assert.notNull(attrValue, "attrValue不能为空");

		this.commonModel.put(attrName, attrValue);
	}

	@Override
	public void afterCompletion(HttpServletRequest paramHttpServletRequest,
			HttpServletResponse paramHttpServletResponse, Object paramObject, Exception paramException)
			throws Exception {

		ActionInfo info = this.context.getActionInfo();
		info.showDebugMsg();
	}

	@Override
	public void postHandle(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse,
			Object paramObject, ModelAndView paramModelAndView) throws Exception {
	}

	/**
	 * 该过程在执行真正的方法之前被调用，用于检查某个方法的是否需要登录
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object target) throws Exception {
		request.setCharacterEncoding("UTF-8");

		// 在request增加各类的 attr
		this.addDebugModeAttr(request);// 将是否是debug模式传给页面
		this.addBasePathAttr(request);// 将页面地址传给页面
		this.addCommonAttrToRequest(request);// 将其他的共用的数据传给页面
		this.context.addMsgFromPrevPage(request);

		// 先获取目标方法的信息
		String defaultLoginUrl = this.context.getProp().getLoginUrl();// 默认的登录url
		ActionInfo info = ActionInfo.create(request, target, defaultLoginUrl);
		this.context.saveActionInfo(info);

		// 如果需要做ajax跨域处理，就增加相应的header
		if (info.isAjax()) {
			/** Ajax跨域header */
			WebUtils.addCrossDomainHeader(response, request);
		}

		// 执行额外的检查
		for (ILoginCheckExtService extService : extCheckServiceList) {
			try {
				// 执行检查
				extService.beforeCheckRight(info, response, request);
			} catch (LoginExtCheckException e) {
				// 如果需要跳转，就跳转
				if (e.isNeedRedirect()) {
					response.sendRedirect(e.getRedirectUrl());
				}
				return false;
			}
		}

		try {

			this.context.checkRight(info.getLoginCheck());
		} catch (BaseApiException ex) {
			if (info.isAjax()) {
				// 如果是ajax，就输出json格式的错误信息
				this.writeErrorResponse(response, ex);
			} else {
				// 如果是普通的页面调用，不需要分开两种返回信息
				response.sendRedirect(info.getLoginUrl());
			}
			return false;
		}

		// 执行额外的检查
		for (ILoginCheckExtService extService : extCheckServiceList) {
			try {
				// 执行检查
				extService.afterCheckRight(info, response, request);
			} catch (LoginExtCheckException e) {
				// 如果需要跳转，就跳转
				if (e.isNeedRedirect()) {
					response.sendRedirect(e.getRedirectUrl());
				}
				return false;
			}
		}

		return true;
	}

	/**
	 * 设置访问url
	 * 
	 * @param request
	 */
	private void addBasePathAttr(HttpServletRequest request) {
		StringBuffer basePath = new StringBuffer();

		// http://xxx.com
		basePath.append(request.getScheme()).append("://").append(request.getServerName());

		if (request.getServerPort() != 80) {
			// 如果端口是80，就不需要加上 ":80"的字样
			basePath.append(":").append(request.getServerPort());
		}

		basePath.append(request.getContextPath()).append("/");

		request.setAttribute(VoNames.BASE_PATH, basePath.toString());
	}

	/**
	 * 在request中增加其他属性
	 */
	private void addCommonAttrToRequest(HttpServletRequest request) {
		for (Map.Entry<String, Object> en : this.commonModel.entrySet()) {
			request.setAttribute(en.getKey(), en.getValue());
		}
	}

	/**
	 * 设置Debug状态
	 * 
	 * @param request
	 */
	private void addDebugModeAttr(HttpServletRequest request) {
		request.setAttribute(VoNames.IS_DEBUG, this.debugModeProperties.isDebugMode());
	}

	/**
	 * 将错误信息用json格式回到客户端
	 * 
	 * @param response
	 * @param ex
	 */
	private void writeErrorResponse(HttpServletResponse response, BaseApiException ex) {
		try {

			CommonErrorResponse res = ExceptionUtil.getErrorResponse(response, ex,
					this.debugModeProperties.isDebugMode());

			response.getWriter().print(JSON.toJSONString(res));
		} catch (IOException e) {
			log.warn("HttpServletResponse.getWriter() 时出错了 ", e);
		}
	}

	protected String getReloginUrl(HttpServletRequest request, ANeedCheckLogin loginCheck) {
		return request.getContextPath() + "/" + loginCheck.loginUrl();
	}

	@PostConstruct
	protected void init() {
		// 获取额外的检查器
		Map<String, ILoginCheckExtService> extCheckServiceMap = this.applicationContext
				.getBeansOfType(ILoginCheckExtService.class);
		this.extCheckServiceList.addAll(extCheckServiceMap.values());

		if (log.isDebugEnabled()) {
			for (ILoginCheckExtService service : extCheckServiceList) {
				log.debug("LoginCheckInterceptor 找到额外的检查器 {}", service.getClass().getName());
			}
		}
	}
}