package com.cfido.commons.spring.apiServer.ws;

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
@ComponentScan(basePackageClasses = WsApiServerAutoConfig.class)
public class WsApiServerAutoConfig {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WsApiServerAutoConfig.class);

	public WsApiServerAutoConfig() {
		log.debug("自动配置  ApiServerAutoConfig");
	}

}
