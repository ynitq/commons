package com.cfido.commons.utils.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * <pre>
 * 为跨域ajax请求增加header
 * </pre>
 * 
 * @author 梁韦江
 *  2016年7月6日
 */
public class CrossDomainInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse,
			Object paramObject, Exception paramException) throws Exception {
	}

	@Override
	public void postHandle(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse,
			Object paramObject, ModelAndView paramModelAndView) throws Exception {
	}

	@Override
	public boolean preHandle(HttpServletRequest paramHttpServletRequest, HttpServletResponse resp,
			Object paramObject) throws Exception {

		/** Ajax跨域header */
		resp.setHeader("Cache-Control", "no-cache");
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Methods", "*");
		resp.setHeader("Access-Control-Allow-Headers", "x-requested-with");

		return true;
	}

}
