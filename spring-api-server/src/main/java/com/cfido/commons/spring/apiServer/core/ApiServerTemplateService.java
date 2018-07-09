package com.cfido.commons.spring.apiServer.core;

import com.cfido.commons.spring.utils.BaseTemplateService;

/**
 * <pre>
 * Freemarker模板引擎
 * </pre>
 * 
 */
public class ApiServerTemplateService extends BaseTemplateService {

	@Override
	protected String getTemplatePath(String templateName) {
		return String.format("templates/api_server/%s.ftl", templateName);
	}

}
