package com.cfido.commons.spring.apiServer.service;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfido.commons.spring.apiServer.core.DebugPageVo;
import com.cfido.commons.spring.debugMode.DebugModeProperties;

import freemarker.template.TemplateException;

/**
 * <pre>
 * 开发时调试用的的controller
 * </pre>
 * 
 * @author 梁韦江 2016年7月4日
 */
@RequestMapping("/dev")
@Controller
public class ApiDebugController {

	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

	private final String apiUtlPrefix = "/api";

	@Autowired
	private DebugModeProperties debugMode;

	@Autowired
	private ApiMapContainer apiMapContainer;

	@Autowired
	private ApiServerTemplateService templateService;

	@RequestMapping("/")
	@ResponseBody
	public String index(ModelMap model, String token) throws TemplateException, IOException {

		// 调试页面的vo
		DebugPageVo vo = this.apiMapContainer.getDebugPageVo(apiUtlPrefix);

		model.addAttribute("vo", vo);
		model.addAttribute("pageTitle", this.getPageTitle());
		model.addAttribute("token", token);
		model.addAttribute("apiServerUrl", this.getApiServerUrl());

		if (this.debugMode.isDebugMode()) {
			return this.templateService.process("index", model);
		} else {
			return "isDebugMode=false";
		}
	}

	public String getPageTitle() {
		return "xxx";
	}

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
