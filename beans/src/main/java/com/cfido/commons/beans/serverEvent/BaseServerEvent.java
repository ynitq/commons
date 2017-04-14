package com.cfido.commons.beans.serverEvent;

/**
 * <pre>
 * 服务器推送给客户端的基类，
 * 
 * 目前是用于 MQTT发送数据时，对要发送的数据进行包装。
 * 
 * 客户端收到数据后，先按 BaseServerEvent 解析，然后根据className再用实现类解析
 * </pre>
 * 
 * @author 梁韦江
 *  2016年9月4日
 */
public class BaseServerEvent {

	/** 推送数据的类型，就是数据的class的名字 */
	private String className;

	private final long createTime = System.currentTimeMillis();

	public BaseServerEvent() {
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public long getCreateTime() {
		return createTime;
	}

}
