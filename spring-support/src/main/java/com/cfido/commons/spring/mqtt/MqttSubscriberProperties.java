package com.cfido.commons.spring.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <pre>
 * MQTT 消息订阅者服务
 * 
 * 参数在 application.properties 中配置
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月11日
 */
@ConfigurationProperties(prefix = "mqtt.sub")
public class MqttSubscriberProperties {

	private String username = "hncloud_sub";
	private String password = "lz_sub";

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
