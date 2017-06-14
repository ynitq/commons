package com.cfido.commons.spring.sendMail;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * SendMailAutoConfig自动化配置，配置完成后，可使用{@link SendMailService}服务，用于发送邮件
 * </pre>
 * 
 * @author 梁韦江 2016年8月26日
 */
@Configuration
@ComponentScan(basePackageClasses = SendMailAutoConfig.class)
public class SendMailAutoConfig {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SendMailAutoConfig.class);

	public SendMailAutoConfig() {
		log.debug("自动配置  SendMailAutoConfig");
	}

}
