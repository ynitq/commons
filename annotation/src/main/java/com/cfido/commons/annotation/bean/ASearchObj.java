package com.cfido.commons.annotation.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.linzi.common.enums.SearchInputType;

/**
 * <pre>
 * 列表字段标识
 * </pre>
 * 
 * @author 黄云
 * 2015年11月11日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE , ElementType.FIELD })
@Documented
public @interface ASearchObj {
	public abstract SearchInputType type() default SearchInputType.TEXT;
	public abstract Class<?> selectEnum() default Object.class;
	public abstract String voField() default "";
}
