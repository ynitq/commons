package com.cfido.commons.spring.jmxInWeb.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cfido.commons.spring.dict.core.DictCoreService;
import com.cfido.commons.spring.jmxInWeb.core.JwInfImpl;

/**
 * <pre>
 * jmx in web 的 所有页面
 * </pre>
 * 
 * @author 梁韦江
 */
@RequestMapping(value = "/jmxInWeb")
abstract class BaseJmxInWebController {

	@Autowired
	private DictCoreService coreService;

	@Autowired
	protected JwTemplateService templateService;

	@Autowired
	private JwInfImpl infImpl;

	/**
	 * 返回有共用属性的model
	 */
	protected Map<String, Object> createCommonModel() {
		Map<String, Object> model = new HashMap<>();
		model.put("pageTitle", this.coreService.getSystemName());
		model.put("adminInProp", this.infImpl.isAdminInPorp());
		return model;
	}

}
