package com.cfido.commons.codeGen.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.cfido.commons.codeGen.CodeGenAutoConfig;
import com.cfido.commons.spring.jmxInWeb.JmxInWebAutoConfig;

/**
 * <pre>
 * 激活生成entity功能的注解
 * </pre>
 * 
 * @author 梁韦江 2016年9月11日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = {
		CodeGenAutoConfig.class,
		JmxInWebAutoConfig.class,
})
public @interface CodeGenEntity {

}
