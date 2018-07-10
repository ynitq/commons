package com.cfido.commons.spring.apiServer.ajax;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * api server 自动化配置
 * </pre>
 * 
 * @author 梁韦江 2017年4月14日
 */
@Configuration
@ComponentScan(basePackageClasses = AjaxApiServerAutoConfig.class)
public class AjaxApiServerAutoConfig {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AjaxApiServerAutoConfig.class);

	public AjaxApiServerAutoConfig() {
		log.debug("自动配置  ApiServerAutoConfig");
	}

}
