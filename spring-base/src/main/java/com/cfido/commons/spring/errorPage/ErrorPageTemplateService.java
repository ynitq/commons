package com.cfido.commons.spring.errorPage;

import org.springframework.stereotype.Service;

import com.cfido.commons.spring.utils.BaseTemplateService;

/**
 * <pre>
 * FreeMarker模板处理，用于根据Model和View模板合成页面
 * </pre>
 * 
 * @author<a href="https://github.com/liangwj72">Alex (梁韦江)</a> 2015年9月13日
 */
@Service
public class ErrorPageTemplateService extends BaseTemplateService {

	// private static final org.slf4j.Logger log =
	// org.slf4j.LoggerFactory.getLogger(ErrorPageTemplateService.class);

	@Override
	protected String getTemplatePath(String templateName) {
		return templateName;
	}
}
