package com.cfido.commons.annotation.other;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 声明一个类是一个常量
 * </pre>
 * 
 * @author 梁韦江
 */
@Target({
		ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AConstants {

}
