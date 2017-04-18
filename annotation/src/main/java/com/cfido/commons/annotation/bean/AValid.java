package com.cfido.commons.annotation.bean;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cfido.commons.enums.ValidEnum;

/**
 * <pre>
 * 验证标识
 * </pre>
 * 
 * @author 黄云
 * 2015年11月11日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE , ElementType.FIELD })
@Documented
public @interface AValid {
	
	public abstract ValidEnum[] valids() default {ValidEnum.REQUIRED};
	
	public abstract String param() default "";//辅助参数--如eq的字段名
	
}
