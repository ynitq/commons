package com.cfido.commons.spring.jmx;

import com.ynitq.utils.jmxInWeb.spring.BaseStringBootTemplate;

/**
 * <pre>
 * MBean服务，可注册mbean，但spring boot都是自动的，所以基本上啥都不用做
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月11日
 */
public class JmxInWebService extends BaseStringBootTemplate {

	private int port;

	@Override
	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
