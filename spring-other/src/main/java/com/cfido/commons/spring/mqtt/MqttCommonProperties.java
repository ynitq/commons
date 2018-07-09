package com.cfido.commons.spring.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <pre>
 * mqtt通用配置
 * 
 * 参数在 application.properties 中配置
 * </pre>
 * 
 * @author 梁韦江
 *  2016年9月4日
 */
@ConfigurationProperties(prefix = "mqtt.common")
public class MqttCommonProperties {

	/** topic 的前缀， 每个应用有专门的主题，前缀是不一样的，在服务器上 /etc/mosquitto/alcfile中定义 */
	private String topicPrefix = "app/hncloud/";

	/** 服务地址 */
	private String host = "192.168.100.10";

	/** 端口 */
	private int port = 1883;

	/** 是否清空 session,如果为true，无法接受离线消息 */
	private boolean cleanSession = false;

	/** 是否自动重连 */
	private boolean automaticReconnect = true;

	/** 连接的超时时间 */
	private int connectionTimeout = 10;

	/** 多少秒发送一个保持连接的心跳包 */
	private int keepAliveInterval = 60;

	/** 最多可以有多少个消息等待确认，qos=1的时候，服务器对记录有多少个消息为被客户端确认，如果数量太对，就没法发送新消息 */
	private int maxInflight = 100;

	/** 协议的版本号 */
	private int mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1;

	public String getTopicPrefix() {
		return topicPrefix;
	}

	/**
	 * 主题前缀必须以 "/" 结尾
	 * 
	 * @param topicPrefix
	 */
	public void setTopicPrefix(String topicPrefix) {
		if (topicPrefix.endsWith("/")) {
			this.topicPrefix = topicPrefix;
		} else {
			this.topicPrefix = topicPrefix + "/";
		}

	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	public boolean isAutomaticReconnect() {
		return automaticReconnect;
	}

	public void setAutomaticReconnect(boolean automaticReconnect) {
		this.automaticReconnect = automaticReconnect;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	public int getMaxInflight() {
		return maxInflight;
	}

	public void setMaxInflight(int maxInflight) {
		this.maxInflight = maxInflight;
	}

	public int getMqttVersion() {
		return mqttVersion;
	}

	public void setMqttVersion(int mqttVersion) {
		this.mqttVersion = mqttVersion;
	}

}
