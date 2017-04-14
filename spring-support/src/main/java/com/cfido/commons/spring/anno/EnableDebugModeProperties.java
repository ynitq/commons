package com.cfido.commons.spring.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.cfido.commons.spring.debugMode.DebugModeAutoConfig;

/**
 * 自动配置 DebugModeProperties
 * 
 * <pre>
 * 配置完成后，目前仅仅就是有一个DebugModeProperties可用
 * 
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月26日
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DebugModeAutoConfig.class)
public @interface EnableDebugModeProperties {

}
