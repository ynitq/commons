package com.cfido.commons.annotation.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 写注释，用于生成代码时，生成帮助文字
 * </pre>
 * 
 * @author 黄云 2015年12月14日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE , ElementType.FIELD, ElementType.METHOD })
@Documented
public @interface AComment {
	
	String value() default "";// 字段注释
	
}
