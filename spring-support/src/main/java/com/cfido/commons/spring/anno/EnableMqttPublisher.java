package com.cfido.commons.spring.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.cfido.commons.spring.mqtt.MqttCommonProperties;
import com.cfido.commons.spring.mqtt.MqttPublisherConfig;
import com.cfido.commons.spring.mqtt.MqttPublisherProperties;
import com.cfido.commons.spring.mqtt.MqttPublisherService;

/**
 * 自动配置MQTT 消息发布者
 * 
 * <pre>
 * 配置完成后，可使用 {@link MqttPublisherService} 发布信息
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月11日
 * 
 * @see MqttPublisherProperties 发布者的参数
 * @see MqttCommonProperties 通用参数
 * @see MqttPublisherService 信息发布服务
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MqttPublisherConfig.class)
public @interface EnableMqttPublisher {

}
