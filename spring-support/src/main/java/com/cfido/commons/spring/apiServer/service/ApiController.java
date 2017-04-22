package com.cfido.commons.spring.apiServer.service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

import com.alibaba.fastjson.JSON;
import com.cfido.commons.beans.apiExceptions.ApiNotFoundException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.apiServer.BaseResponse;
import com.cfido.commons.spring.apiServer.core.ApiMethodInfo;
import com.cfido.commons.spring.debugMode.DebugModeProperties;
import com.cfido.commons.spring.security.LoginContext;
import com.cfido.commons.utils.utils.ExceptionUtil;
import com.cfido.commons.utils.utils.StringUtils;
import com.cfido.commons.utils.web.BinderUtil;
import com.cfido.commons.utils.web.WebUtils;

/**
 * <pre>
 * Ajax接入方式的基类，用的是spring mvc
 * 
 * 带有自动判断JSONP的功能。
 * 其实 JSONP的判断可以直接用 {@link AbstractJsonpResponseBodyAdvice}，但这个类会导致界面不好看...
 * 在这个 颜值就是正义 的世界，我们还是做好看点吧。
 * 
 * </pre>
 * 
 * @author 梁韦江 2016年6月30日
 */
@RestController
@RequestMapping("/api")
public class ApiController {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ApiController.class);

	@Autowired
	private ApiMapContainer apiMapContainer;

	@Autowired
	private DebugModeProperties debugMode;

	@Autowired(required = false)
	private LoginContext loginContext;

	/** 额外的安全服务，如果能找到，就用这个作为安全服务，否则就直接用loginContext */
	@Autowired(required = false)
	private IApiControllerSecurityService securityService;

	/**
	 * 用于自动判断是否是jsonp的情况
	 */
	protected String getJsonpParamName() {
		return "callback";
	}

	@PostConstruct
	protected void init() {
		if (this.securityService != null) {
			log.info("{} 有特殊的安全服务者 {}", this.getClass().getSimpleName(), this.securityService.getClass().getSimpleName());
		} else {
			if (this.loginContext == null) {
				log.warn("警告！！ {} 没有任何安全保障，请检查");
			}
		}
	}

	/**
	 * 进入invoke时，先执行一个钩子方法，让子类有更过的空间增加功能
	 * 
	 * @param req
	 *            req
	 * @param resp
	 *            resp
	 * @param methodInfo
	 *            ApiMethodInfo
	 */
	private void onBeforeInvoke(HttpServletRequest req, HttpServletResponse resp, ApiMethodInfo methodInfo)
			throws BaseApiException {

		if (methodInfo.isNeedLogin()) {
			if (this.securityService != null) {
				this.securityService.onBeforeInvoke(req, resp, methodInfo);
			} else {
				// 否则就用LoginCheck的安全检查
				methodInfo.checkRights(this.loginContext);
			}
		}
	}

	@RequestMapping(value = "/{infName}/{methodName}")
	@ResponseBody
	public String invoke(@PathVariable String infName, @PathVariable String methodName, HttpServletRequest req,
			HttpServletResponse resp) {

		/** Ajax跨域header */
		resp.setHeader("Cache-Control", "no-cache");
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Headers", "x-requested-with");

		ApiMethodInfo methodInfo = this.apiMapContainer.findApiMethod(infName, methodName);

		BaseResponse res = null;
		Object param = null;

		long start = System.currentTimeMillis();
		try {
			try {
				if (methodInfo == null) {
					// 找不到api就直接抛错
					throw new ApiNotFoundException(infName, methodName);
				}

				if (log.isDebugEnabled()) {
					StringBuffer sb = new StringBuffer();
					WebUtils.debugRequest(req, sb);

					log.debug("准备调用接口: {}/{} \n\tRequest中的信息:{}", infName, methodName, sb.toString());
				}

				// 进入方法时，先执行一个方法，看看是否要处理一下HttpRequest的东西
				this.onBeforeInvoke(req, resp, methodInfo);

				if (methodInfo.hasParam()) {
					// 如果该接口有参数，就从http请求绑定参数

					// 直接用实现类的类名作为session名字
					String sessionName = methodInfo.getImplObj().getClass().getName();

					param = BinderUtil.bindForm(req, methodInfo.getFormClass(), methodInfo.isSaveFormToSession(),
							BinderUtil.RESET_PARAM_NAME, true, sessionName);
				}

				// 执行接口方法
				res = methodInfo.invoke(param);

				if (res == null && this.debugMode.isDebugMode()) {
					// 如果为null ,并且要生成模拟数据
					res = methodInfo.createMockData();
				}
			} catch (Exception e) {
				res = ExceptionUtil.getErrorResponse(resp, e, log.isDebugEnabled());
			}

			return this.detectJsonp(res, req);
		} finally {

			long end = System.currentTimeMillis();

			if (log.isDebugEnabled()) {
				String paramStr = null;
				if (param != null && !methodInfo.isUploadFile()) {
					// 如果接口有上传文件，不能随便将表单 toJsonString，如果上传个1M的文件...
					paramStr = JSON.toJSONString(param, true);
				}

				String responseStr = res == null ? null : JSON.toJSONString(res, true);
				log.debug("调用接口: {}/{} 时间消耗 :{}ms \n绑定表单的结果:{} \n返回内容:{}", infName, methodName, end - start, paramStr,
						responseStr);
			}
		}
	}

	private String detectJsonp(BaseResponse res, HttpServletRequest request) {
		if (res == null) {
			return null;
		}

		String body = JSON.toJSONString(res, true);
		// 获取jsonp callback的函数名
		String callbackName = request.getParameter(getJsonpParamName());

		if (StringUtils.hasText(callbackName)) {
			// 如果是 jsonp的请求，就包装一下
			return String.format("%s(%s);", callbackName, body);
		} else {
			// 如果不是jsonp,就直接返回内容
			return body;
		}
	}

}
