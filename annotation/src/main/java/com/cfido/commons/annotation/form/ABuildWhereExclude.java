package com.cfido.commons.annotation.form;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 在buildSQL的时候不包含该getter
 * </pre>
 * 
 * @author 梁韦江 2015年7月18日
 */
@Target({
	ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ABuildWhereExclude {

}
