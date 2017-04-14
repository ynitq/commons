package com.cfido.commons.spring.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.cfido.commons.spring.sendMail.SendMailAutoConfig;
import com.cfido.commons.spring.sendMail.SendMailProperties;
import com.cfido.commons.spring.sendMail.SendMailService;

/**
 * 发送邮件服务
 * 
 * </pre>
 * 
 * @author 梁韦江 2016年12月19日
 * 
 * @see SendMailProperties 可配置参数
 * @see SendMailService#sendMail(String, String, String, String) 发送邮件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SendMailAutoConfig.class)
@EnableDebugModeProperties
public @interface EnableSendMailService {

}
