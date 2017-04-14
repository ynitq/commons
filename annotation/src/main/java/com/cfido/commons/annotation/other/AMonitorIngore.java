package com.cfido.commons.annotation.other;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 声明一个ResquestMap不需要被监控系统统计
 * </pre>
 * 
 * @author 梁韦江 2016年12月19日
 */
@Target({
		ElementType.TYPE, ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AMonitorIngore {

}
