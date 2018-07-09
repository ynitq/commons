package com.cfido.commons.spring.weChat;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * 配置提供微信相关服务, 可使用的服务
 * 
 * {@linkplain WeChatProperties} 微信配置参数
 * {@linkplain WeChatService} 微信服务,包括获取ticket，jssdk配置等
 * 
 * </pre>
 * 
 * @see WeChatProperties 配置参数
 * 
 * @author 梁韦江 2016年8月11日
 */
@Configuration
@EnableConfigurationProperties(WeChatProperties.class)
@ComponentScan(basePackageClasses = WeChatAutoConfig.class)
public class WeChatAutoConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WeChatAutoConfig.class);

	public WeChatAutoConfig() {
		log.info("自动配置 微信接入配置服务");
	}

}
