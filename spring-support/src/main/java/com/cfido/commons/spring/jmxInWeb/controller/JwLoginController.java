package com.cfido.commons.spring.jmxInWeb.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import freemarker.template.TemplateException;

/**
 * <pre>
 * jmx in web 的 登录页
 * </pre>
 * 
 * @author 梁韦江
 */
@Controller
public class JwLoginController extends BaseJmxInWebController {

	/**
	 * 登录页
	 * 
	 * @throws IOException
	 * @throws TemplateException
	 */
	@RequestMapping(value = "login")
	@ResponseBody
	public String login() throws TemplateException, IOException {
		Map<String, Object> model = this.createCommonModel();

		return this.templateService.process("login", model);
	}

}
