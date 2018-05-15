package com.cfido.commons.spring.weChatProxy;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.cfido.commons.spring.weChat.WeChatService;

/**
 * <pre>
 * 配置提供微信公开号认证和请求转发服务
 * 
 * {@linkplain WeChatProxyProperties} 微信配置参数
 * {@linkplain WeChatService} 微信服务,包括获取ticket，jssdk配置等
 * 
 * </pre>
 * 
 * @see WeChatProxyProperties 配置参数
 * 
 * @author 梁韦江 2016年8月11日
 */
@Configuration
@EnableConfigurationProperties(WeChatProxyProperties.class)
@ComponentScan(basePackageClasses = WeChatProxyAutoConfig.class)
public class WeChatProxyAutoConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WeChatProxyAutoConfig.class);

	public WeChatProxyAutoConfig() {
		log.info("自动配置 微信接入认证和代理服务");
	}

}
