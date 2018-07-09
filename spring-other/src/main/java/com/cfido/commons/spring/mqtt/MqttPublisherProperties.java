package com.cfido.commons.spring.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <pre>
 * MQTT 消息发布者 配置参数
 * 
 * 参数在 application.properties 中配置
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月11日
 */
@ConfigurationProperties(prefix = "mqtt.pub")
public class MqttPublisherProperties {

	private String username = "hncloud_pub";
	private String password = "lz11223344";
	private String clientId;

	/**
	 * <pre>
	 * 0:最多发1次
	 * 1:只发1次
	 * 2:至少发1次
	 * </pre>
	 */
	private int qos = 1;

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}

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

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}
