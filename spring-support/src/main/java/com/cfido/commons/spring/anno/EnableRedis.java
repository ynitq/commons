package com.cfido.commons.spring.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import com.cfido.commons.spring.redis.RedisConfig;

/**
 * 自动设置 redis
 * 
 * <pre>
 * 主要就是设置redis的序列化工具
 * 
 * 需要用Spring.redis等参数设置好 redis
 * 
 * 配置好以后，用本注解激活，就可使用 RedisTemplate了
 * </pre>
 * 
 * @see RedisTemplate
 * 
 * @author 梁韦江
 *  2016年8月11日
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RedisConfig.class)
public @interface EnableRedis {

}
