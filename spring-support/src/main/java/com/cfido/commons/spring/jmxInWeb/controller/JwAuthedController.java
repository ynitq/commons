package com.cfido.commons.spring.jmxInWeb.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfido.commons.beans.apiExceptions.InvalidLoginStatusException;
import com.cfido.commons.beans.apiServer.BaseApiException;
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

	@Autowired
	private HttpServletResponse response;

	/**
	 * MBean列表，登录后的默认页面
	 * 
	 * @throws TemplateException
	 * @throws IOException
	 */
	@RequestMapping(value = "/")
	@ResponseBody
	public String mbeanList() throws TemplateException, IOException, BaseApiException {
		Map<String, Object> model = this.createCommonModelForUserAndMenu(MENU_MBEAN);
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
	public String mbeanInfo(String objectName) throws TemplateException, IOException, InvalidLoginStatusException {

		if (StringUtils.isEmpty(objectName)) {
			// 如果objectname为空, 就重定向会首页
			this.response.sendRedirect("/jmxInWeb/");
			return null;
		} else {

			Map<String, Object> model = this.createCommonModelForUserAndMenu(MENU_MBEAN);

			model.put("objectName", objectName);

			return this.templateService.process("mbeanInfo", model);
		}
	}

}
