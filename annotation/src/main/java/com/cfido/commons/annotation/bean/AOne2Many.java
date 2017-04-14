package com.cfido.commons.annotation.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * One2Many标识
 * </pre>
 * 
 * @author 黄云
 * 2015年11月11日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE , ElementType.FIELD })
@Documented
public @interface AOne2Many {
	
	public abstract String manyName() default "";//Many端名称
	
	public abstract boolean userBtn() default false;//是否需要按钮链接
	
}
