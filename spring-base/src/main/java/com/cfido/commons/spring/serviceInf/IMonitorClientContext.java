package com.cfido.commons.spring.serviceInf;

/**
 * <pre>
 * 监控客户端的接口
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public interface IMonitorClientContext {

	/**
	 * 用于汇报给中心服务器：访问次数+1
	 */
	void addRequest();
}
