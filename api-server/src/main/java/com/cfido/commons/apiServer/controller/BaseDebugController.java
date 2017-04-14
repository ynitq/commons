package com.cfido.commons.apiServer.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfido.commons.apiServer.TemplateService;
import com.cfido.commons.apiServer.adapter.ApiMapContainer;
import com.cfido.commons.apiServer.vo.DebugPageVo;

import freemarker.template.TemplateException;

/**
 * <pre>
 * 开发时调试用的的controller
 * </pre>
 * 
 * @author 梁韦江 2016年7月4日
 */
// 由子类写注解
// @RequestMapping("/dev")
public abstract class BaseDebugController {

	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

	@RequestMapping("/")
	@ResponseBody
	public String index(Model model, String token) throws TemplateException, IOException {

		// 调试页面的vo
		DebugPageVo vo = ApiMapContainer.getInstance().getDebugPageVo(getApiUrlPrefix());

		model.addAttribute("vo", vo);
		model.addAttribute("pageTitle", this.getPageTitle());
		model.addAttribute("token", token);
		model.addAttribute("apiServerUrl", this.getApiServerUrl());

		if (log.isDebugEnabled()) {
			return TemplateService.getInstance().process("index", model);
		} else {
			return "log.isDebugEnabled()=false";
		}
	}

	/**
	 * 返回子类在 @RequestMapping 定义的url前缀
	 * 
	 * @return
	 */
	public abstract String getApiUrlPrefix();

	public abstract String getPageTitle();

	/**
	 * 获得api server的路径，用于配置跨域调试
	 * 
	 * @return
	 */
	public String getApiServerUrl() {
		return "";
	}

	@RequestMapping("headers")
	@ResponseBody
	public String printHeader(HttpServletRequest request) {

		// 获取所有的header
		List<String> list = new LinkedList<>();
		Enumeration<String> e = request.getHeaderNames();
		while (e.hasMoreElements()) {
			String name = e.nextElement();

			list.add(name + ":\t" + request.getHeader(name));
		}

		// 排序
		Collections.sort(list);

		StringBuffer sb = new StringBuffer();
		for (String str : list) {
			sb.append(str);
			sb.append("\n");
		}

		if (log.isDebugEnabled()) {
			log.debug(sb.toString());
		}

		return sb.toString();
	}
}
