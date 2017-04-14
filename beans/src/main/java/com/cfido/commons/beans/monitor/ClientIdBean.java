package com.cfido.commons.beans.monitor;

/**
 * <pre>
 * 用于保存客户端id的信息
 * 
 * 通过 “启动程序名 + 启动端口 + 本机IP”，可构成对当前系统的唯一标示
 * </pre>
 * 
 * @author 梁韦江 2016年12月16日
 */
public class ClientIdBean {

	/** 启动程序 */
	private String startClassName;

	/** 端口 */
	private int port;

	/** ip地址，可以在配置中设置 */
	private String host;

	private String contextPath = "/";

	public String getStartClassName() {
		return startClassName;
	}

	public void setStartClassName(String startClassName) {
		this.startClassName = startClassName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		if (contextPath != null) {
			this.contextPath = contextPath;
		}
	}

	@Override
	public String toString() {
		return String.format("启动程序=%s 本机地址=%s:%d%s",
				this.startClassName,
				this.host,
				this.port,
				this.contextPath);
	}

}
