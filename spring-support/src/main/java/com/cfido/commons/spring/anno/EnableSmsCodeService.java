package com.cfido.commons.spring.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.cfido.commons.spring.sms.SmsCodeAutoConfig;
import com.cfido.commons.spring.sms.SmsCodeProperties;

/**
 * 短信服务
 * 
 * <pre>
 * 使用异步方式发送验证码
 * 使用redis类做数据存储
 * 
 * 配置后，就可以使用 SmsCodeService 发送验证码和校验验证码
 * 
 * 依赖：
 * 需要用 Redis 依赖 , 配置文件中需要有redis的配置
 * 
 * groupId = org.springframework.boot
 * artifactId = spring-boot-starter-redis
 * 
 * </pre>
 * 
 * @author 梁韦江
 * 
 * @see SmsCodeProperties 可配置参数
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SmsCodeAutoConfig.class)
@EnableDebugModeProperties
@EnableRedis
public @interface EnableSmsCodeService {

}
