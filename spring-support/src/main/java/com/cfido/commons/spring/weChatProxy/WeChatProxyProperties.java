package com.cfido.commons.spring.weChatProxy;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <pre>
 * 微信代理 相关配置参数
 * 
 * 例如:
 * 设置key1转发到 http://www1.xxx.com
 * 设置key2转发到 http://www2.xxx.com
 * 
 * wechatProxy.map.key1=http://www1.xxx.com 
 * wechatProxy.map.key2=http://www2.xxx.com 
 * 
 * 参数在 application.properties 中配置
 * </pre>
 * 
 * @author 梁韦江
 */
@ConfigurationProperties(prefix = "wechatProxy")
public class WeChatProxyProperties {

	private Map<String, String> map;

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

}
