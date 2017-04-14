package com.cfido.commons.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.cfido.commons.spring.anno.EnableDebugModeProperties;

/**
 * <pre>
 * {@link LoginCheckInterceptor}的自动化配置
 * </pre>
 * 
 * @author 梁韦江 2016年8月26日
 */
@Configuration
@EnableDebugModeProperties
@EnableConfigurationProperties(LoginCheckProperties.class)
@ComponentScan(basePackageClasses = LoginCheckAutoConfig.class)
public class LoginCheckAutoConfig extends WebMvcConfigurerAdapter {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoginCheckAutoConfig.class);

	public LoginCheckAutoConfig() {
		log.debug("自动配置 LoginCheckAutoConfig");
	}

	/**
	 * 创建 LoginCheckInterceptor
	 * 
	 * @return
	 */
	@Autowired
	private LoginCheckInterceptor loginCheckInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		log.debug("注入  LoginCheckInterceptor 到mvc中");

		registry.addInterceptor(this.loginCheckInterceptor);
	}

}
