package com.cfido.commons.spring.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.cfido.commons.spring.apiServer.ApiServerAutoConfig;
import com.cfido.commons.spring.debugMode.DebugModeAutoConfig;
import com.cfido.commons.spring.dict.DictAutoConfig;
import com.cfido.commons.spring.errorPage.ErrorPageAutoConfig;
import com.cfido.commons.spring.imageUpload.ImageUploadAutoConfig;
import com.cfido.commons.spring.jmxInWeb.JmxInWebAutoConfig;
import com.cfido.commons.spring.monitor.MonitorClientAutoConfig;
import com.cfido.commons.spring.security.LoginCheckAutoConfig;

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
		DebugModeAutoConfig.class, // debug mode组件，用于设置调试模式
		DictAutoConfig.class, // 字典组件，用于管理页面上的key
		LoginCheckAutoConfig.class, // loginCheck简易安全框架
		JmxInWebAutoConfig.class, // JMX 页面ui
		ErrorPageAutoConfig.class, // 错误页面处理
		MonitorClientAutoConfig.class, // 系统监控
		ImageUploadAutoConfig.class,// 图片上传
		ApiServerAutoConfig.class, // api server
})
public @interface EnableWebPorjectSet {

}
