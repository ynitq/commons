package com.cfido.commons.spring.jmxInWeb.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cfido.commons.beans.apiExceptions.InvalidLoginStatusException;
import com.cfido.commons.spring.dict.core.DictCoreService;
import com.cfido.commons.spring.security.CommonAdminWebUser;
import com.cfido.commons.spring.security.LoginContext;
import com.cfido.commons.spring.security.RememberMeUserHandler;
import com.cfido.commons.utils.html.MenuVo;

/**
 * <pre>
 * Controller的基类
 * </pre>
 * 
 * @author 梁韦江
 */
@RequestMapping(value = "/jmxInWeb")
abstract class BaseJmxInWebController {

	/** 菜单：MBean */
	public final static String MENU_MBEAN = "MBean";

	@Autowired(required = false)
	private DictCoreService dictCoreService;

	@Autowired
	protected JwTemplateService templateService;

	@Autowired
	private RememberMeUserHandler adminUserHander;

	@Autowired
	protected LoginContext loginContext;

	private MenuVo menuVo;

	/**
	 * 返回有共用属性的model
	 */
	protected Map<String, Object> createCommonModel() {
		Map<String, Object> model = new HashMap<>();
		model.put("pageTitle", this.dictCoreService != null ? this.dictCoreService.getSystemName() : "JmxInWeb");
		model.put("adminInProp", this.adminUserHander.isAdminInPorp());
		return model;
	}

	/**
	 * 返回有共用属性的model, 包含用户和菜单属性
	 * 
	 * @throws InvalidLoginStatusException
	 */
	protected Map<String, Object> createCommonModelForUserAndMenu(String menuId) throws InvalidLoginStatusException {
		Map<String, Object> model = this.createCommonModel();

		if (this.menuVo == null) {
			this.menuVo = this.createMenu();
		}

		// 生成该用户有权限的菜单对象
		CommonAdminWebUser user = this.loginContext.getUser(CommonAdminWebUser.class);
		if (user == null) {
			throw new InvalidLoginStatusException();
		}
		MenuVo menu = this.menuVo.createForUser(user);

		// 设置菜单中的当前菜单
		menu.setCurMenuById(menuId);

		// 将用户传给页面
		model.put("user", user);

		// 将菜单传给页面
		model.put("menuVo", menu);

		return model;
	}

	/**
	 * 菜单配置
	 */
	private MenuVo createMenu() {
		MenuVo menuVo = new MenuVo();

		menuVo.addMenu(MENU_MBEAN, "MBean", "index");

		return menuVo;

	}
}
