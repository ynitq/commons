package com.cfido.commons.spring.mqtt;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

import com.cfido.commons.utils.utils.StringUtils;

/**
 * <pre>
 * 信息发布者
 * </pre>
 * 
 * @author 梁韦江 2016年9月4日
 */
public abstract class BaseMqttService implements MqttCallback {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BaseMqttService.class);

	private final MqttConnectOptions options = new MqttConnectOptions();

	protected MqttClient client;

	/**
	 * 连接服务器
	 * 
	 * @throws MqttSecurityException
	 * @throws MqttException
	 */
	@ManagedOperation(description = "连接服务器")
	public void connect() throws MqttSecurityException, MqttException {
		if (!this.client.isConnected()) {
			this.client.connect(this.options);
		}
	}

	@Override
	public void connectionLost(Throwable ex) {
		log.debug("{}.connectionLost", this.getClass().getSimpleName());
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		try {
			if (token != null && token.getMessage() != null) {
				log.debug("deliveryComplete: token={}", token.getMessage());
			}
		} catch (Exception ex) {
			log.error("deliveryComplete时，出现错误", ex);
		}
	}

	@ManagedOperation(description = "断开与服务器的连接")
	public void disConnect() throws MqttSecurityException, MqttException {
		if (this.client.isConnected()) {
			this.client.disconnect();
		}
	}

	@PreDestroy
	public void close() throws MqttSecurityException, MqttException {
		try {
			this.disConnect();

			this.client.close();
		} catch (NullPointerException e) {
			// 我们用的这个工具有bug，如果client一次都没有被用过，关闭时会报空指针
		}
	}

	@ManagedAttribute(description = "连接MQTT服务器的ClientId")
	public abstract String getClientId();

	@ManagedAttribute(description = "连接MQTT服务器的密码")
	public abstract String getPassword();

	@ManagedAttribute(description = "连接MQTT服务器的用户名")
	public abstract String getUsername();

	@ManagedAttribute(description = "是否已经连接到服务器")
	public boolean isConnected() {
		return this.client.isConnected();
	}

	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception {
		log.debug("messageArrived: topic={}, msg={}", topic, msg);

	}

	/**
	 * 设置好链接配置
	 */
	private void configOption() {

		// 认证设置
		options.setUserName(this.getUsername());// 用户名
		options.setPassword(this.getPassword().toCharArray());// 密码

		// 如果是用集群，可通过 options.setServerURIs 设置多服务器

		MqttCommonProperties prop = this.getMqttCommonProperties();

		// 通用属性设置
		options.setCleanSession(prop.isCleanSession());
		options.setAutomaticReconnect(prop.isAutomaticReconnect());
		options.setKeepAliveInterval(prop.getKeepAliveInterval());
		options.setMaxInflight(prop.getMaxInflight());
		options.setMqttVersion(prop.getMqttVersion());
		options.setConnectionTimeout(prop.getConnectionTimeout());
	}

	protected abstract MqttCommonProperties getMqttCommonProperties();

	/**
	 * 获得实际的topic名
	 * 
	 * @param old
	 * @return
	 */
	public String getRealTopicName(String old) {
		return this.getMqttCommonProperties().getTopicPrefix() + old;
	}

	@PostConstruct
	protected void init() throws MqttException {

		if (StringUtils.isEmpty(this.getClientId())) {
			throw new RuntimeException("clientId不能为空");
		}

		String url = String.format("tcp://%s:%d", this.getMqttCommonProperties().getHost(),
				this.getMqttCommonProperties().getPort());

		// 配置持久化
		MemoryPersistence memoryPersistence = new MemoryPersistence();

		// 配置各项参数
		this.configOption();

		// 创建MQTT客户端
		this.client = new MqttClient(url, this.getClientId(), memoryPersistence);

		client.setCallback(this);

		this.onInitFinished();

	}

	/**
	 * 初始化完成后
	 */
	protected void onInitFinished() {
		log.info("{} 初始化 url={}", this.getClass().getSimpleName(), this.client.getServerURI());
	}
}
