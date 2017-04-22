package com.cfido.commons.spring.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.cfido.commons.beans.serverEvent.BaseServerEvent;
import com.cfido.commons.utils.utils.StringUtils;

/**
 * <pre>
 * 信息发布者
 * </pre>
 * 
 * @author 梁韦江
 *  2016年9月4日
 */
@ManagedResource(description = "MQTT发布服务", objectName = "MQTT服务:name=MqttPublisherService")
@Service
public class MqttPublisherService extends BaseMqttService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MqttPublisherService.class);

	@Autowired
	private MqttCommonProperties commonProp;

	@Autowired
	private MqttPublisherProperties prop;

	@Override
	public String getClientId() {

		if (StringUtils.isEmpty(this.prop.getClientId())) {
			throw new RuntimeException("请配置 mqtt.pub.clientId, clientId是每个发布者的唯一的标识符，不能为空");
		}

		return this.prop.getClientId();
	}

	@Override
	public String getPassword() {
		return this.prop.getPassword();
	}

	@Override
	public String getUsername() {
		return this.prop.getUsername();
	}

	/**
	 * <pre>
	 * 发送消息到指定的主题
	 * 
	 * 如果retained为真，表示该消息将保留下来，新用户订阅该主题时，这条消息将发送给新订阅者
	 * 
	 * </pre>
	 * 
	 * @param topic
	 *            主题名
	 * @param plainText
	 *            要推送的字符串明文
	 * @param retained
	 *            一般情况下都是false
	 * @return
	 */
	@ManagedOperation(description = "发送消息")
	@ManagedOperationParameters({
			@ManagedOperationParameter(description = "主题名", name = "topic"),
			@ManagedOperationParameter(description = "要发送的内容", name = "plainText"),
			@ManagedOperationParameter(description = "是否保存为最后一条", name = "retained")
	})
	public boolean publish(String topic, String plainText, boolean retained) {
		Assert.hasText(topic, "主题不能为空");
		Assert.hasText(plainText, "内容不能为空");

		try {
			this.connect();

			MqttMessage mqttMsg = new MqttMessage(plainText.getBytes());
			mqttMsg.setRetained(retained);
			mqttMsg.setQos(this.prop.getQos());

			this.client.publish(this.getRealTopicName(topic), mqttMsg);

			return true;
		} catch (MqttException e) {
			log.error("出现了mqtt类型的错误", e);
			return false;
		}
	}

	/**
	 * 推送数据给客户端
	 * 
	 * @param topic
	 *            主题名
	 * @param event
	 *            要推送的数据
	 * @param retained
	 *            是否保存为最后一个消息
	 * @return
	 */
	public boolean publish(String topic, BaseServerEvent event, boolean retained) {
		if (event == null || topic == null) {
			return false;
		}

		event.setClassName(event.getClass().getName());
		String json = JSON.toJSONString(event);

		return this.publish(topic, json, retained);
	}

	/**
	 * 推送数据给客户端，默认是不保存为最后一个消息
	 * 
	 * @param topic
	 * @param event
	 * @return
	 */
	public boolean publish(String topic, BaseServerEvent event) {
		return this.publish(topic, event, false);
	}

	@Override
	protected MqttCommonProperties getMqttCommonProperties() {
		return this.commonProp;
	}

}
