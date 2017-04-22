package com.cfido.commons.loginCheck;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于在web中检查用户是否已经登录
 * 
 * <pre>
 * 即将有spring secure替换
 * </pre>
 * 
 * @author liangwj
 * 
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ANeedCheckLogin {

	/**
	 * 要检查的在http session中的用户类的类型
	 * 
	 * @return
	 */
	Class<? extends IWebUser> userClass();

	/**
	 * 要检查的权限id，如果是-1表示任何权限
	 * 
	 * @return
	 */
	String optId() default "";// 权限id

	/**
	 * 登录超时的时候需要跳转过去的URL
	 * 
	 * @return
	 */
	String loginUrl() default "";
}
