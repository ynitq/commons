package com.cfido.commons.spring.sms;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置提供sms服务需要的组件和参数
 * 
 * @author 梁韦江
 */
@Configuration
@EnableConfigurationProperties(SmsCodeProperties.class)
@ComponentScan(basePackageClasses = SmsCodeAutoConfig.class)
public class SmsCodeAutoConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SmsCodeAutoConfig.class);

	public SmsCodeAutoConfig() {
		log.info("自动配置 短信验证码服务");
	}
}
