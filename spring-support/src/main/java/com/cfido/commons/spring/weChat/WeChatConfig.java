package com.cfido.commons.spring.weChat;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 配置提供微信相关服务
 * 
 * @see WeChatProperties 配置参数
 * 
 * @author 梁韦江
 *  2016年8月11日
 */
@Configuration
@EnableConfigurationProperties(WeChatProperties.class)
public class WeChatConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WeChatConfig.class);

	public WeChatConfig() {
		log.info("自动配置 微信接入配置服务");
	}

}
