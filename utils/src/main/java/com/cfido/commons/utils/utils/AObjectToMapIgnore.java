package com.cfido.commons.utils.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 在ClassUtil执行  objectToMap 时，是否忽略
 * </pre>
 * 
 * @author 梁韦江 2016年10月12日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
		ElementType.TYPE
})
@Documented
public @interface AObjectToMapIgnore {

}
