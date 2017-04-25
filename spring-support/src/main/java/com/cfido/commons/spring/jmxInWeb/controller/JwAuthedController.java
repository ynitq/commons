package com.cfido.commons.spring.jmxInWeb.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfido.commons.loginCheck.ANeedCheckLogin;
import com.cfido.commons.spring.jmxInWeb.core.JwWebUser;

import freemarker.template.TemplateException;

/**
 * <pre>
 * 登录以后可以看到的所有页面,都是有权限要求的
 * </pre>
 * 
 * @author 梁韦江
 */
@Controller
@ANeedCheckLogin(userClass = JwWebUser.class, loginUrl = "/jmxInWeb/login")
public class JwAuthedController extends BaseJmxInWebController {
	
	/**
	 * MBean列表，登录后的默认页面
	 * 
	 * @throws TemplateException
	 * @throws IOException
	 */
	@RequestMapping(value = "/")
	@ResponseBody
	public String mbeanList() throws TemplateException, IOException {
		Map<String, Object> model = this.createCommonModel();
		return this.templateService.process("mbeanList", model);
	}

	/**
	 * MBean列表，登录后的默认页面
	 * 
	 * @throws TemplateException
	 * @throws IOException
	 */
	@RequestMapping(value = "/mbeanInfo")
	@ResponseBody
	public String mbeanInfo() throws TemplateException, IOException {
		Map<String, Object> model = this.createCommonModel();
		return this.templateService.process("mbeanInfo", model);
	}

}
