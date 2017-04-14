package com.cfido.commons.annotation.form;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 什么这个方法是用于校验form
 * 用于 BinderUtil, 在request将中的参数设置到一个form时，如果某方法有这个注解，就自动运行一次
 * </pre>
 * 
 * @author liangwj
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface AFormValidateMethod {

}
