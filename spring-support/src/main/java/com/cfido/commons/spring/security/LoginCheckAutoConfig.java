package com.cfido.commons.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.ExpiringSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.cfido.commons.spring.anno.EnableDebugModeProperties;
import com.cfido.commons.utils.utils.LRULinkedHashMap;

/**
 * <pre>
 * {@link LoginCheckInterceptor}的自动化配置
 * 为了满足ajax跨域请求，我们需要实现能从header中获取sessionId，所以我们用了spring session项目
 * 
 * </pre>
 * 
 * @author 梁韦江 2016年8月26日
 * 
 * @see SessionRepositoryFilter
 * 
 */
@Configuration
@EnableDebugModeProperties
@EnableSpringHttpSession
@EnableConfigurationProperties(LoginCheckProperties.class)
@ComponentScan(basePackageClasses = LoginCheckAutoConfig.class)
public class LoginCheckAutoConfig extends WebMvcConfigurerAdapter {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoginCheckAutoConfig.class);

	/**
	 * 默认的session时间（单位秒）：1天
	 */
	public static final int SESSION_MAX_INACTIVE_INTERVAL_SECONDS = 86400;

	/** 我们用map来保存session，所以要限制一下map的大小 */
	public static final int SESSION_MAP_LIMIT = 10000;

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

	/**
	 * 系统默认其实是使用redis的，但我们这里用Map
	 */
	@Bean
	public MapSessionRepository sessionRepository() {
		// TODO 这里写死了用Map来保存session，以后要修改为可以使用Redis

		LRULinkedHashMap<String, ExpiringSession> map = new LRULinkedHashMap<>(SESSION_MAP_LIMIT);
		
		MapSessionRepository sessionRepository = new MapSessionRepository(map);
		sessionRepository.setDefaultMaxInactiveInterval(SESSION_MAX_INACTIVE_INTERVAL_SECONDS);

		return sessionRepository;
	}

	@Bean
	public HttpSessionStrategy httpSessionStrategy() {
		// session获取策略是：先header再cookie
		return new HeaderAndCookieHttpSessionStrategy();
	}
}
