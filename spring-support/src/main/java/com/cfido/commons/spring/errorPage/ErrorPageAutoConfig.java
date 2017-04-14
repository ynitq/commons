package com.cfido.commons.spring.errorPage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.cfido.commons.spring.anno.EnableDebugModeProperties;

/**
 * <pre>
 * 通用错误页面处理器的自动配置文件
 * 
 * </pre>
 * 
 * @author 梁韦江 2016年8月11日
 */
@Configuration
@EnableConfigurationProperties(ErrorPageProperties.class)
@EnableDebugModeProperties
@ComponentScan(basePackageClasses = ErrorPageAutoConfig.class)
public class ErrorPageAutoConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ErrorPageAutoConfig.class);

	public ErrorPageAutoConfig() {
		log.info("自动配置  通用错误页面处理器");
	}
}
