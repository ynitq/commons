package com.cfido.commons.spring.jmx;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <pre>
 * sms相关配置参数
 * 
 * 参数在 application.properties 中配置
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月11日
 */
@ConfigurationProperties(prefix = "jmx")
public class JmxInWebProperties {

	private int port = -1;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
