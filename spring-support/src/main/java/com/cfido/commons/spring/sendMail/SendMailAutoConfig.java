package com.cfido.commons.spring.sendMail;

import java.util.Properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.cfido.commons.spring.debugMode.DebugModeProperties;

/**
 * <pre>
 * SendMailAutoConfig自动化配置
 * </pre>
 * 
 * @see DebugModeProperties 其实就在自动创建这个叫DebugModeProperties的bean
 * 
 * @author 梁韦江 2016年8月26日
 */
@Configuration
@EnableConfigurationProperties(value = {
		SendMailProperties.class,
})
public class SendMailAutoConfig {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SendMailAutoConfig.class);

	public SendMailAutoConfig() {
		log.debug("自动配置  SendMailAutoConfig");
	}

	@Bean
	public JavaMailSenderImpl javaMailSender() {
		JavaMailSenderImpl ret = new JavaMailSenderImpl();
		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.auth", true);
		javaMailProperties.put("mail.smtp.timeout", 60000);
		javaMailProperties.put("mail.smtp.starttls.enable", false);
		javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.SocketFactory");
		javaMailProperties.put("mail.smtp.socketFactory.fallback", false);
		ret.setJavaMailProperties(javaMailProperties);
		return ret;

	}

	@Bean
	public JavaMailSenderImpl javaMailSenderSsl() {
		JavaMailSenderImpl ret = new JavaMailSenderImpl();
		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.auth", true);
		javaMailProperties.put("mail.smtp.timeout", 60000);
		javaMailProperties.put("mail.smtp.starttls.enable", false);
		javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		javaMailProperties.put("mail.smtp.socketFactory.fallback", false);
		ret.setJavaMailProperties(javaMailProperties);
		return ret;
	}
}
