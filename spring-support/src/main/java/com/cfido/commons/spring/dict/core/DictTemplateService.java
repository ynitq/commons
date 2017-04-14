package com.cfido.commons.spring.dict.core;

import org.springframework.stereotype.Service;

import com.cfido.commons.spring.utils.BaseTemplateService;

/**
 * <pre>
 * Freemarker模板引擎
 * </pre>
 * 
 * @author 梁韦江 2016年11月17日
 */
@Service
public class DictTemplateService extends BaseTemplateService {

	@Override
	protected String getTemplatePath(String templateName) {
		return String.format("dict/templates/%s.ftl", templateName);
	}

}
