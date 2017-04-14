package com.cfido.commons.spring.errorPage;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSON;
import com.cfido.commons.beans.apiServer.impl.CommonErrorResponse;
import com.cfido.commons.spring.debugMode.DebugModeProperties;
import com.cfido.commons.utils.utils.ExceptionUtil;
import com.cfido.commons.utils.utils.LogUtil;

/**
 * 通用的错误处理器，自带模板
 * 
 * <pre>
 * 重写BasicErrorController,主要负责系统的异常页面的处理以及错误信息的显示
 * </pre>
 * 
 * @author 梁韦江 2016年8月24日
 */
@Controller
public class ErrorPageController implements ErrorController {

	/** 传给错误页面的VO的名字 */
	public final static String ERROR_INFO_ATTR_NAME = "errorInfo";

	public final static String ERROR_PATH = "/error";

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ErrorPageController.class);

	@Autowired
	private ErrorAttributes errorAttributes;

	private final ErrorPageTemplateService templateService = new ErrorPageTemplateService();

	@Autowired
	private ErrorPageProperties prop;

	@Autowired
	private DebugModeProperties debugModeProperties;

	/**
	 * 处理错误
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return
	 */
	@RequestMapping(ERROR_PATH)
	@ResponseBody
	public String error(HttpServletRequest request, HttpServletResponse response) {

		try {
			ErrorInfoBean errorInfo = this.getErrorInfo(request);

			log.error("在请求 {} 时，发生了错误， 状态码:{}, 错误信息:{}",
					errorInfo.getRequestUri(), errorInfo.getStatusCode(), errorInfo.getMessage());

			boolean isAjax = this.isAjaxRequest(request);
			if (isAjax) {
				// 如果是 ajax 请求，就返回json的响应
				return this.getErrorAjaxResponse(response, errorInfo);
			} else {
				if (errorInfo.isApiException()) {
					// API错误不视为500错误
					response.setStatus(errorInfo.getStatusCode());
				} else {
					response.setStatus(errorInfo.getStatusCode());
				}

				// 如果是页面，就根据模板返回不同的页面
				return this.getErrorPage(errorInfo);
			}
		} catch (Throwable ex) {
			log.error("吐血了，生成错误页面的时候出RuntimeException, 导致错误页面没法正常显示", ex);
			return "出错了";

		}
	}

	/**
	 * 根据状态码，返回不同的错误页面
	 * 
	 * @param errorInfo
	 *            errorInfo
	 * @return 页面内容
	 */
	private String getErrorPage(ErrorInfoBean errorInfo) {
		Map<String, Object> model = new HashMap<>();
		model.put(ERROR_INFO_ATTR_NAME, errorInfo);

		String templatePath = this.prop.getTemplatePath(errorInfo.getStatusCode());
		return this.templateService.process(templatePath, model);
	}

	/**
	 * 根据创痛码，返回不同的json信息
	 * 
	 * @param errorInfo
	 *            errorInfo
	 * @return json信息
	 */
	private String getErrorAjaxResponse(HttpServletResponse response, ErrorInfoBean errorInfo) {

		CommonErrorResponse res;

		response.setStatus(errorInfo.getStatusCode());

		// 如果是Ajax请求
		switch (errorInfo.getStatusCode()) {
		case 404:
			// 如果是404错误，就将url返回过去
			res = new CommonErrorResponse();
			res.setErrorMsg(errorInfo.getRequestUri());
			break;
		case 405:
			// 如果是405错误，就将url返回过去
			res = new CommonErrorResponse();
			res.setErrorMsg(errorInfo.getRequestUri());

			break;

		default:
			// 正常的500错误
			res = ExceptionUtil.getErrorResponse(response, errorInfo.getException(), this.debugModeProperties.isDebugMode());
			break;
		}

		if (this.debugModeProperties.isDebugMode()) {
			// 如果是开发模式，将调查错误过程回馈给客户端
			if (errorInfo.getException() != null) {
				// 如果有错误，就返回调用过程
				res.setDebugMsg(LogUtil.getTraceString(null, errorInfo.getException()));
			} else {
				// 如果没有错误，就返回错误信息
				res.setDebugMsg(errorInfo.getMessage());
			}
		}

		return JSON.toJSONString(res, true);
	}

	/**
	 * 根据 HttpServletRequest，提取错误信息
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return 错误信息
	 */
	private ErrorInfoBean getErrorInfo(HttpServletRequest request) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);

		Throwable ex = this.errorAttributes.getError(requestAttributes);
		String requestUri = this.getAttribute(requestAttributes, "javax.servlet.error.request_uri");
		int status = this.getStatus(request).value();

		ErrorInfoBean bean = new ErrorInfoBean(requestUri, status, ex);
		bean.setDebugMode(this.debugModeProperties.isDebugMode());
		return bean;
	}

	@SuppressWarnings("unchecked")
	private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
		return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}

	/**
	 * 获取错误编码
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return 错误编码
	 */
	private HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		try {
			return HttpStatus.valueOf(statusCode);
		} catch (Exception ex) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

	/**
	 * 我们通过 request的 header判断，而不是用 {@link RequestMapping#produces()}, 因为如果ajax
	 * dataType 是html时，{@link RequestMapping#produces()} 会视为页面请求
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return
	 */
	private boolean isAjaxRequest(HttpServletRequest request) {
		String requestedWith = request.getHeader("X-Requested-With");
		return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
	}
}
