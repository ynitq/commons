package com.cfido.commons.spring.sortedLock;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * api server 自动化配置
 * </pre>
 * 
 * @author 梁韦江 2017年4月14日
 */
@Configuration
@ComponentScan(basePackageClasses = SortedLockAutoConfig.class)
public class SortedLockAutoConfig {
}
