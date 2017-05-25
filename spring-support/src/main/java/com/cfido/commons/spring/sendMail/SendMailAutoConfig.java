package com.cfido.commons.spring.sendMail;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.cfido.commons.spring.debugMode.DebugModeProperties;

/**
 * <pre>
 * SendMailAutoConfig自动化配置
 * </pre>
 * 
 * @see DebugModeProperties 其实就在自动创建这个叫DebugModeProperties的bean
 * 
 * @author 梁韦江 2016年8月26日
 */
@Configuration
@ComponentScan(basePackageClasses = SendMailAccountPool.class)
public class SendMailAutoConfig {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SendMailAutoConfig.class);

	public SendMailAutoConfig() {
		log.debug("自动配置  SendMailAutoConfig");
	}

}
