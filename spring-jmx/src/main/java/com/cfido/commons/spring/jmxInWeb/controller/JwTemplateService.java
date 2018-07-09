package com.cfido.commons.spring.jmxInWeb.controller;

import org.springframework.stereotype.Service;

import com.cfido.commons.spring.utils.BaseTemplateService;

/**
 * <pre>
 * Freemarker模板引擎
 * </pre>
 * 
 * @author 梁韦江
 */
@Service
public class JwTemplateService extends BaseTemplateService {

	@Override
	protected String getTemplatePath(String templateName) {
		return String.format("templates/jmx_in_web/%s.ftl", templateName);
	}

}
