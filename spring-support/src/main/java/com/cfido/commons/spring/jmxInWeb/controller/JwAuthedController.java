package com.cfido.commons.spring.jmxInWeb.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfido.commons.beans.apiExceptions.InvalidLoginStatusException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.loginCheck.ANeedCheckLogin;
import com.cfido.commons.spring.jmxInWeb.core.JmxInWebService;
import com.cfido.commons.spring.jmxInWeb.models.MBeanVo;
import com.cfido.commons.spring.security.CommonAdminWebUser;

import freemarker.template.TemplateException;

/**
 * <pre>
 * 登录以后可以看到的所有页面,都是有权限要求的
 * </pre>
 * 
 * @author 梁韦江
 */
@Controller
@ANeedCheckLogin(userClass = CommonAdminWebUser.class, loginUrl = "/jmxInWeb/login")
public class JwAuthedController extends BaseJmxInWebController {

	@Autowired
	private JmxInWebService service;

	/**
	 * MBean列表，登录后的默认页面
	 * 
	 * @throws TemplateException
	 * @throws IOException
	 */
	@RequestMapping(value = {
			"/", "/index"
	})
	@ResponseBody
	public String mbeanList() throws TemplateException, IOException, BaseApiException {
		Map<String, Object> model = this.createCommonModelForUserAndMenu(MENU_MBEAN);

		model.put("list", this.service.getMBeanList());

		return this.templateService.process("mbeanList", model);
	}

	/**
	 * MBean列表，登录后的默认页面
	 * 
	 * @throws TemplateException
	 * @throws IOException
	 * @throws InvalidLoginStatusException
	 */
	@RequestMapping(value = "/mbeanInfo")
	@ResponseBody
	public String mbeanInfo(String objectName) throws TemplateException, IOException, BaseApiException {

		Map<String, Object> model = this.createCommonModelForUserAndMenu(MENU_MBEAN);
		MBeanVo mbeanVo = this.service.getMBeanInfo(objectName);

		model.put("mbean", mbeanVo);

		return this.templateService.process("mbeanInfo", model);
	}

}
