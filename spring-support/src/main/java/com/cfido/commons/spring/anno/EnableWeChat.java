package com.cfido.commons.spring.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.cfido.commons.spring.weChat.WeChatConfig;
import com.cfido.commons.spring.weChat.WeChatProperties;

/**
 * 微信接入服务
 * 
 * @author 梁韦江
 *  2016年8月11日
 * 
 * @see WeChatProperties 可配置参数
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(WeChatConfig.class)
public @interface EnableWeChat {

}
