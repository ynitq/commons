package com.cfido.commons.spring.jmxInWeb;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * 自动启动jmx的web管理界面
 * 
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月11日
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
