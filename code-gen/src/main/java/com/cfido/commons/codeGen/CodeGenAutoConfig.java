package com.cfido.commons.codeGen;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * 全局的配置
 * </pre>
 * 
 * @author 梁韦江 2016年9月11日
 */
@Configuration
@ComponentScan(basePackageClasses = CodeGenAutoConfig.class)
@EnableConfigurationProperties(CodeGenProperties.class)
public class CodeGenAutoConfig {
}
