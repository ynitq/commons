package com.cfido.commons.spring.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.util.Assert;

/**
 * <pre>
 * 信息发布者
 * </pre>
 * 
 * @author 梁韦江
 *  2016年9月4日
 */
@ManagedResource(description = "MQTT订阅者")
public class MqttSubscriber extends BaseMqttService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MqttSubscriber.class);

	private final String clientId;

	private final IMessageListener messageListener;
	private final MqttCommonProperties commonProperties;
	private final String password;
	private final String username;

	/**
	 * 构建订阅者
	 * 
	 * @param commonProperties
	 *            mqtt属性
	 * @param clientId
	 *            客户端标示
	 * @param username
	 *            连接服务器的用户名
	 * @param password
	 *            连接服务器的密码
	 * @param messageListener
	 *            处理消息的监听器
	 */
	protected MqttSubscriber(MqttCommonProperties commonProperties,
			String clientId, String username, String password,
			IMessageListener messageListener) {
		super();
		this.messageListener = messageListener;
		this.clientId = clientId;
		this.username = username;
		this.password = password;
		this.commonProperties = commonProperties;
	}

	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception {
		// 必须拦截错误，否则会影响 ack回应包的发送，导致不停的断线重连
		try {
			if (this.messageListener != null) {
				this.messageListener.onMessageArrived(topic, msg.toString());
			}
		} catch (Throwable ex) {
			log.error("调用 messageListener 处理收到的数据时出错了", ex);
		}
	}

	/**
	 * 订阅主题
	 * 
	 * @param topic
	 *            主题名字
	 */
	@ManagedOperation(description = "订阅主题")
	@ManagedOperationParameters({
			@ManagedOperationParameter(description = "主题名", name = "topic"),
	})
	public boolean subscribe(String topic) {

		Assert.hasText(topic);

		try {
			this.connect();

			if (this.client.isConnected()) {
				this.client.subscribe(this.getRealTopicName(topic));
				return true;
			} else {
				log.debug("未连接上服务器，无法订阅");
				return false;
			}
		} catch (MqttException ex) {
			log.error("出现了mqtt类型的错误", ex);
			return false;
		}
	}

	@Override
	protected MqttCommonProperties getMqttCommonProperties() {
		return this.commonProperties;
	}

}
