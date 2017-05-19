package com.cfido.commons.spring.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.cfido.commons.spring.jmxInWeb.JmxInWebAutoConfig;
import com.cfido.commons.spring.monitor.MonitorClientAutoConfig;

/**
 * 自动配置 web项目的常用组件
 * 
 * @author 梁韦江 2016年11月30日
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = {
		JmxInWebAutoConfig.class, // JMX 页面ui
		MonitorClientAutoConfig.class, // 系统监控
})
public @interface EnableWebPorjectSet {

}
