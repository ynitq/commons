package com.cfido.commons.annotation.form;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 用于声明这个表单或者表单中的这个setter不允许html
 * </pre>
 * 
 * @author 梁韦江 2017年6月1日
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
		ElementType.METHOD, ElementType.TYPE
})
@Documented
public @interface AFormNotHtml {
}
