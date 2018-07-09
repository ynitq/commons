package com.cfido.commons.spring.jmxInWeb;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 用于为MBean domain这种排序顺序的注解
 * </pre>
 * 
 * @author 梁韦江 2017年4月27日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ADomainOrder {
	/** 排序序号，从大到小排序 */
	int order();

	/** domain的名字 */
	String domainName();
}
