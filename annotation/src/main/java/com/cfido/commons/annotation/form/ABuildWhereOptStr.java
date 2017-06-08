package com.cfido.commons.annotation.form;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 通过反射的方法搜索字段，并且创建sql的时候使用的
 * 
 * 如果getter上有该声明，表示需要其他操作符，默认是 =
 * </pre>
 * 
 * @author 梁韦江 2015年7月18日
 */
@Target({
		ElementType.METHOD, ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ABuildWhereOptStr {
	/** 操作符 */
	String optStr() default "=";

	/** 默认如果是like的情况下只在值的后面加 %，例如 "张三%", 如果该值为真，表示前后修改要加 %，例如 “%张三%” */
	boolean mustBeAllLike() default false;
}
