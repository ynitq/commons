package com.cfido.commons.spring.debugMode;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;

/**
 * <pre>
 * Debug模式 配置
 * 
 * 默认参数
 * debug.debugMode=false // 默认值是当前log.isDebugEnabled()
 * debug.cssName=debug_css
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月26日
 */
@ConfigurationProperties(prefix = "debug")
@ManagedResource(description = "Debug模式 配置", objectName = "com.cfido.commons.spring.debugMode:name=DebugModeProperties")
@ADomainOrder(order = CommonMBeanDomainNaming.ORDER, domainName = CommonMBeanDomainNaming.DOMAIN)
public class DebugModeProperties {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DebugModeProperties.class);

	public static final String DEBUG_CSS = "debug_css";

	/** 是否调试模式 */
	private boolean debugMode = log.isDebugEnabled();

	/** debug模式下用的css名字 */
	private String cssName = "debug_css";

	@ManagedAttribute(description = "是否处于Debug模式：debug.debugMode")
	public boolean isDebugMode() {
		return debugMode;
	}

	@ManagedAttribute()
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public String getCssName() {
		return cssName;
	}

	public void setCssName(String cssName) {
		this.cssName = cssName;
	}

	@ManagedOperation(description = "Shutdown，模拟正常退出。")
	public void shutdown() {
		System.exit(0);
	}
}
