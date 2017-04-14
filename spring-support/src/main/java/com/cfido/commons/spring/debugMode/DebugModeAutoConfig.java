package com.cfido.commons.spring.debugMode;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * DebugMode自动化配置, 其实就是参数一个存储配置的类而已
 * </pre>
 * 
 * @see DebugModeProperties 其实就在自动创建这个叫DebugModeProperties的bean
 * 
 * @author 梁韦江 2016年8月26日
 */
@Configuration
@EnableConfigurationProperties(value = {
		DebugModeProperties.class,
})
public class DebugModeAutoConfig {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DebugModeAutoConfig.class);

	public DebugModeAutoConfig() {
		log.debug("自动配置  DebugModeAutoConfig");
	}
}
