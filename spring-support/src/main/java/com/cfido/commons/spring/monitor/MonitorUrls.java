package com.cfido.commons.spring.monitor;

/**
 * <pre>
 * url常量
 * </pre>
 * 
 * @author 梁韦江 2016年12月16日
 */
public interface MonitorUrls {
	// 服务器端接受报告的url
	String SERVER_REPORT = "report";

	// 客户端让服务器回调，获取运行情况的url
	String CLIENT_CALLBACK = "/monitor/check";
}
