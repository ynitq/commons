package com.cfido.commons.spring.mqtt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 * mqtt 消息发布者 配置
 * 
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月11日
 */
@Configuration
@EnableConfigurationProperties(value = {
		MqttPublisherProperties.class,
		MqttCommonProperties.class
})
public class MqttPublisherConfig {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MqttPublisherConfig.class);

	public MqttPublisherConfig() {
		log.info("自动配置 MQTT 消息发布者服务");
	}

	@Bean
	public MqttPublisherService mqttPublisherService() {
		return new MqttPublisherService();
	}

}
