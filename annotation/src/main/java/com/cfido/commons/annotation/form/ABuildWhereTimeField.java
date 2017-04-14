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
 * 如果getter上有该声明，表示是描述时间的字段
 * </pre>
 * 
 * @author 梁韦江 2015年7月18日
 */
@Target({
		ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ABuildWhereTimeField {
	boolean isBegin() default true;
	String filed() default "";
}
