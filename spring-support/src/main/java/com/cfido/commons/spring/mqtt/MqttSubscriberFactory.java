package com.cfido.commons.spring.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * <pre>
 * 信息发布者
 * </pre>
 * 
 * @author 梁韦江
 *  2016年9月4日
 */
@Service
public class MqttSubscriberFactory {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MqttSubscriberFactory.class);

	@Autowired
	private MqttCommonProperties commonProp;

	@Autowired
	private MqttSubscriberProperties prop;

	public MqttSubscriberFactory() {
		super();
	}

	/**
	 * 创建订阅者对象
	 * 
	 * @param clientId
	 *            clientId
	 * @param messageListener
	 *            接受消息的监听器
	 * @return 订阅者对象
	 * @throws MqttException
	 *             MqttException错误
	 */
	public MqttSubscriber createSubscriber(String clientId, IMessageListener messageListener) throws MqttException {

		Assert.hasText(clientId, "clientId不能为空");

		log.debug("创建新订阅者 {}", clientId);

		MqttSubscriber res = new MqttSubscriber(
				commonProp,
				clientId,
				this.prop.getUsername(), this.prop.getPassword(),
				messageListener);

		res.init();

		return res;
	}

}
