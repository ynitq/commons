package com.cfido.commons.spring.errorPage;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cfido.commons.spring.debugMode.DebugModeAutoConfig;

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
@ComponentScan(basePackageClasses = ErrorPageAutoConfig.class)
@Import(value = {
		DebugModeAutoConfig.class, // debugMode
})

public class ErrorPageAutoConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ErrorPageAutoConfig.class);

	public ErrorPageAutoConfig() {
		log.info("自动配置  通用错误页面处理器");
	}
}
