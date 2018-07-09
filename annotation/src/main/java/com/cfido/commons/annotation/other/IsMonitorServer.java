package com.cfido.commons.annotation.other;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 是否是监控中心的服务器，有这个配置时，监控客户端不会启动
 * </pre>
 * 
 * @author 梁韦江
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IsMonitorServer {

}
