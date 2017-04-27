package com.cfido.commons.spring.jmxInWeb.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfido.commons.spring.security.CommonAdminWebUser;

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

	@Autowired
	private HttpServletResponse response;

	/**
	 * 登录页
	 * 
	 * @throws IOException
	 * @throws TemplateException
	 */
	@RequestMapping(value = "login")
	@ResponseBody
	public String login() throws TemplateException, IOException {

		if (this.loginContext.getUser(CommonAdminWebUser.class) != null) {
			// 如果已经登录了，就直接跳转
			this.response.sendRedirect("index");
			return null;
		} else {

			Map<String, Object> model = this.createCommonModel();

			return this.templateService.process("login", model);
		}
	}

	/**
	 * logout
	 * 
	 * @throws IOException
	 * @throws TemplateException
	 */
	@RequestMapping(value = "logout")
	public void logout() throws TemplateException, IOException {
		this.loginContext.onLogout(CommonAdminWebUser.class);
		response.sendRedirect("login");
	}
}
