package com.cfido.commons.annotation.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * API接口的类注解
 * </pre>
 * 
 * @author 黄云
 * 2016年6月24日
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AClass {
	
	/**
	 * 类名
	 * @return
	 */
	String value() default "";
}
