package com.cfido.commons.spring.dict;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * <pre>
 * 字典模块自动化配置
 * </pre>
 * 
 * @see DictProperties 配置文件
 * 
 * @author 梁韦江 2016年8月26日
 */
@Configuration
@EnableConfigurationProperties(DictProperties.class)
@ComponentScan(basePackageClasses = DictAutoConfig.class)
public class DictAutoConfig extends WebMvcConfigurerAdapter {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DictAutoConfig.class);

	public DictAutoConfig() {
		log.debug("自动配置  DictAutoConfig");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 字典管理界面的静态资源
		registry.addResourceHandler("/dict/static/**").addResourceLocations("classpath:/dict/static/");

		super.addResourceHandlers(registry);
	}
}
