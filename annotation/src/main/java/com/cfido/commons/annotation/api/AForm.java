package com.cfido.commons.annotation.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 声明这个类是form，用于生成界面和校验
 * </pre>
 * 
 * @author 梁韦江
 *  2016年6月29日
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AForm {

	/**
	 * 备注信息，说明这个表单是干什么的，用于自动生成的页面
	 * 
	 * @return
	 */
	String value() default "";
}
