package com.cfido.commons.spring.jmxInWeb;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.cfido.commons.spring.apiServer.ajax.AjaxApiServerAutoConfig;
import com.cfido.commons.spring.debugMode.DebugModeAutoConfig;
import com.cfido.commons.spring.dict.DictAutoConfig;
import com.cfido.commons.spring.errorPage.ErrorPageAutoConfig;
import com.cfido.commons.spring.imageUpload.ImageUploadAutoConfig;
import com.cfido.commons.spring.jmxInWeb.core.JmxInWebService;
import com.cfido.commons.spring.security.LoginCheckAutoConfig;

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
@Import(value = {
		ImageUploadAutoConfig.class, // 图片上传
		DictAutoConfig.class, // 字典组件，用于管理页面上的key
		DebugModeAutoConfig.class, // debug mode组件，用于设置调试模式
		LoginCheckAutoConfig.class, // loginCheck简易安全框架
		ErrorPageAutoConfig.class, // 错误页面处理
		AjaxApiServerAutoConfig.class, // api server
})

public class JmxInWebAutoConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JmxInWebAutoConfig.class);

	public JmxInWebAutoConfig() {
		log.info("自动配置 JmxInWeb");
	}
}
