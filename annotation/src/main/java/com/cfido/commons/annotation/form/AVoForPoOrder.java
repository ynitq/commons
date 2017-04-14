package com.cfido.commons.annotation.form;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * VoForPo 的帮助注解，用于级联查询出两个以上的相同po的语句中给po定于赋值解析排序的
 * </pre>
 * 
 * @author 黄云
 * 2015-8-10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface AVoForPoOrder {

	int order();//解析顺序
	
	String className();//类名（po的类型名）
	
}
