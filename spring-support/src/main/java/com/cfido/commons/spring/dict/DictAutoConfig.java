package com.cfido.commons.spring.dict;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.cfido.commons.apiServer.adapter.ApiMapContainer;
import com.cfido.commons.spring.dict.inf.impl.DictAdminUserImpl;
import com.cfido.commons.spring.dict.inf.impl.DictAttachmentManagerImpl;
import com.cfido.commons.spring.dict.inf.impl.DictManagerImpl;

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

	public final static String ATTACHMENT_PATH = "dict/attachment";
	// public final static String ATTACHMENT_PATH = "dict";

	public DictAutoConfig() {
		log.debug("自动配置  DictAutoConfig");
	}

	@Autowired
	private DictManagerImpl manager;

	@Autowired
	private DictAttachmentManagerImpl attachmentManagerImpl;

	@Autowired
	private DictAdminUserImpl adminUserImpl;

	@PostConstruct
	public void init() throws Exception {

		log.info("增加字典管理接口到 api server");
		ApiMapContainer.getInstance().addImplToMap(manager);
		ApiMapContainer.getInstance().addImplToMap(adminUserImpl);
		ApiMapContainer.getInstance().addImplToMap(attachmentManagerImpl);

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
