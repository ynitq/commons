package com.cfido.commons.annotation.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * api模拟器自动生成调用的返回时，可以用这个注解声明一下mock数据。
 * 
 * 这个注解只用在getter上
 * </pre>
 * 
 * @author 梁韦江
 *  2016年6月22日
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AMock {
	/**
	 * 生成模拟数据时的默认值
	 * 
	 * @return
	 */
	String value() default "";

	/**
	 * 如果setter中间的内容是list，这里可设置list.size，默认是10
	 * 
	 * @return
	 */
	int size() default 10;

	/**
	 * 是否是id,如果是，在生成模拟数据时，自动设置i+1的值
	 * 
	 * @return
	 */
	boolean id() default false;
}
