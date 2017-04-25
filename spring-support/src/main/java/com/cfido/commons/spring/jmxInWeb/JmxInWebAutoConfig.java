package com.cfido.commons.spring.jmxInWeb;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.cfido.commons.spring.jmxInWeb.core.JmxInWebService;

/**
 * <pre>
 * 自动配置jmx的web管理界面
 * 
 * </pre>
 * 
 * @see JmxInWebService 配置后可以使用该服务注册和反注册mbean
 * 
 * @author 梁韦江 2016年8月11日
 */
@Configuration
@ComponentScan(basePackageClasses = JmxInWebAutoConfig.class)
@EnableConfigurationProperties(JmxInWebProperties.class)
public class JmxInWebAutoConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JmxInWebAutoConfig.class);

	public JmxInWebAutoConfig() {
		log.info("自动配置 JmxInWeb");
	}
}
