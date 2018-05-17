package com.cfido.commons.annotation.other;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.cfido.commons.enums.WeChatAuthScope;

/**
 * <pre>
 * 是否需要微信授权
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
		ElementType.METHOD
})
@Documented
public @interface AWechatRequestAuth {
	/** 授权的类型 */
	WeChatAuthScope scope();
}
