package com.cfido.commons.spring.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.cfido.commons.spring.mqtt.MqttCommonProperties;
import com.cfido.commons.spring.mqtt.MqttSubcriberConfig;
import com.cfido.commons.spring.mqtt.MqttSubscriber;
import com.cfido.commons.spring.mqtt.MqttSubscriberFactory;
import com.cfido.commons.spring.mqtt.MqttSubscriberProperties;

/**
 * 自动配置MQTT 消息订阅者
 * 
 * <pre>
 * 配置完成后，可使用 {@link MqttSubscriberFactory} 创建不同id的订阅者
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月11日
 * 
 * @see MqttSubscriberProperties 订阅者的参数
 * @see MqttCommonProperties 通用参数
 * @see MqttSubscriberFactory 订阅者创建工厂
 * @see MqttSubscriber 创建出来的订阅者对象
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MqttSubcriberConfig.class)
public @interface EnableMqttSubscriber {

}
