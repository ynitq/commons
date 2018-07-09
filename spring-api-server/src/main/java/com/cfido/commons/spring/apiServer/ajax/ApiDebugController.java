package com.cfido.commons.spring.apiServer.ajax;

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

import com.cfido.commons.spring.apiServer.core.ApiServerTemplateService;
import com.cfido.commons.spring.apiServer.core.DebugPageVo;
import com.cfido.commons.spring.debugMode.DebugModeProperties;
import com.cfido.commons.spring.dict.core.DictCoreService;
import com.cfido.commons.spring.utils.WebContextHolderHelper;
import com.cfido.commons.utils.utils.EncodeUtil;
import com.cfido.commons.utils.web.WebUtils;

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

	private final ApiServerTemplateService templateService = new ApiServerTemplateService();

	@Autowired(required = false)
	private DictCoreService dictCoreService;

	@RequestMapping("/")
	@ResponseBody
	public String index(ModelMap model, HttpServletRequest request) throws TemplateException, IOException {

		// 调试页面的vo
		DebugPageVo vo = this.apiMapContainer.getDebugPageVo(apiUtlPrefix);

		model.addAttribute("vo", vo); // 根据反射接口实现类生成的 页面vo

		// 其他通用的内容
		model.addAttribute("sessionId", request.getSession().getId());
		model.addAttribute("basePath", WebContextHolderHelper.getBasePath());

		if (this.dictCoreService != null) {
			model.addAttribute("pageTitle", this.dictCoreService.getSystemName());
			this.dictCoreService.addToModel(model);
		}

		if (this.debugMode.isDebugMode()) {
			return this.templateService.process("index", model);
		} else {
			return "isDebugMode=false";
		}
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
		sb.append("来源ip:").append(WebUtils.findRealRemoteAddr(request));

		for (String str : list) {
			sb.append("\n");
			sb.append(str);
		}

		if (log.isDebugEnabled()) {
			log.debug(sb.toString());
		}

		return EncodeUtil.html(sb.toString(), false);
	}
}
