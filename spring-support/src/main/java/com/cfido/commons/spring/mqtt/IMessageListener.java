package com.cfido.commons.spring.mqtt;

/**
 * <pre>
 * 当有消息的时候
 * </pre>
 * 
 * @author 梁韦江
 *  2016年9月4日
 */
public interface IMessageListener {

	void onMessageArrived(String topic, String content);

}
