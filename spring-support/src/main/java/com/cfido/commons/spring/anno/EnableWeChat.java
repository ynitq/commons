package com.cfido.commons.spring.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.cfido.commons.spring.redis.RedisConfig;
import com.cfido.commons.spring.weChat.WeChatAutoConfig;
import com.cfido.commons.spring.weChat.WeChatProperties;

/**
 * 微信接入服务, 微信的accessToken保存在 redis中，所以必须有redis的配置
 * 
 * @author 梁韦江 2016年8月11日
 * 
 * @see WeChatProperties 可配置参数
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = {
		WeChatAutoConfig.class, RedisConfig.class
})
public @interface EnableWeChat {

}
