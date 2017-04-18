package com.cfido.commons.annotation.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cfido.commons.enums.PageType;

/**
 * <pre>
 * 管理模块名称
 * </pre>
 * 
 * @author 黄云
 * 2015年11月11日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface AModelName {

	public abstract String modelName();
	
	public abstract String pageName();
	
	public abstract String visitPage() default "";
	
	public abstract PageType pageType() default PageType.ALL;
	
	public abstract boolean useParam() default false;
	
}
