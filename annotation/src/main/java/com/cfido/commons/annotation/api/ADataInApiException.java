package com.cfido.commons.annotation.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * 标准在BaseApiExcetion子类的属性上,用于告知apiServer，这些属性需要传递到前端
 * </pre>
 * 
 * @author 梁韦江 2017年6月2日
 * 
 */
@Target({
		ElementType.FIELD
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ADataInApiException {

}
