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

	public final static String ATTACHMENT_PATH = "work/dict_attachment";
	// public final static String ATTACHMENT_PATH = "dict";

	public DictAutoConfig() {
		log.debug("自动配置  DictAutoConfig");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/dict/static/**").addResourceLocations("classpath:/dict/static/");

		// 附件
		String attachmentPathPatterns = String.format("/%s/**", ATTACHMENT_PATH);
		String attachmentResourceLocations = String.format("file:%s/", ATTACHMENT_PATH);
		registry.addResourceHandler(attachmentPathPatterns).addResourceLocations(attachmentResourceLocations);

		super.addResourceHandlers(registry);
	}
}
