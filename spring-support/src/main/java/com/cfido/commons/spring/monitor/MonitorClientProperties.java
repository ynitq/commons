/*
 * 文件名：[文件名]
 * 版权：〈版权〉
 * 描述：〈描述〉
 * 修改人：〈修改人〉
 * 修改时间：YYYY-MM-DD
 * 修改单号：〈修改单号〉
 * 修改内容：〈修改内容〉
 */
package com.cfido.commons.spring.monitor;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import com.cfido.commons.utils.utils.NetUtil;

/**
 * 在配置文件中的监控配置，包括服务器IP、访问的http接口
 * 
 * <pre>
 * monitorClient.server.host = 192.168.100.10
 * monitorClient.server.port = 30000
 * 
 * monitorClient.client.host 自动配置本地IP，多IP时会识别不准，所以需要手动配置
 * monitorClient.client.port 自动配置
 * 
 * monitorClient.enable = true
 * monitorClient.enableCenterUser=false
 * 
 * monitorClient.report.retryDelay = 5
 * monitorClient.report.retryWhenFail = true
 * </pre>
 * 
 * @author wjc
 * @date 2016年8月17日
 *
 */
@ConfigurationProperties(prefix = "monitorClient")
public class MonitorClientProperties {

	public static class ClientInfo {
		private String host;
		private int port = 0;

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public void setPort(int port) {
			this.port = port;
		}
	}

	/** 报告相关 */
	public static class Report {
		/** 报告失败时，重试的时间间隔 （分钟） */
		private long retryDelay = 5;

		/** 报告失败时，是否重试 */
		private boolean retryWhenFail = true;

		public long getRetryDelay() {
			return retryDelay;
		}

		public boolean isRetryWhenFail() {
			return retryWhenFail;
		}

		public void setRetryDelay(long retryDelay) {
			this.retryDelay = retryDelay;
		}

		public void setRetryWhenFail(boolean retryWhenFail) {
			this.retryWhenFail = retryWhenFail;
		}

	}

	public static class ServerInfo {
		private String host;
		private int port = 30000;

		public String getHost() {
			return host;
		}

		public int getPort() {
			return port;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public void setPort(int port) {
			this.port = port;
		}
	}

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MonitorClientProperties.class);

	/** 服务器的配置 */
	private final ServerInfo server = new ServerInfo();

	/** 客户端的配置 */
	private final ClientInfo client = new ClientInfo();

	/** 和报告相关的配置 */
	private final Report report = new Report();

	/** 是否激活 */
	private boolean enable = true;

	private String md5Key = "我是好人";

	/** 是否使用中心的用户系统 */
	private boolean enableCenterUser = false;

	public MonitorClientProperties() {
	}

	public ClientInfo getClient() {
		return client;
	}

	public String getMd5Key() {
		return md5Key;
	}

	public Report getReport() {
		return report;
	}

	public ServerInfo getServer() {
		return server;
	}

	/**
	 * 服务器的url-汇报
	 */
	public String getServerUrlOfReport() {
		return String.format("http://%s:%s/%s",
				this.server.getHost(),
				this.server.getPort(),
				MonitorUrls.SERVER_REPORT);
	}

	/**
	 * 服务器的url-用户信息
	 */
	public String getServerUrlOfUser() {
		return String.format("http://%s:%s/%s",
				this.server.getHost(),
				this.server.getPort(),
				MonitorUrls.SERVER_USER);
	}

	public boolean isEnable() {
		return enable;
	}

	public boolean isEnableCenterUser() {
		return enableCenterUser;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public void setEnableCenterUser(boolean enableCenterUser) {
		this.enableCenterUser = enableCenterUser;
	}

	public void setMd5Key(String md5Key) {
		this.md5Key = md5Key;
	}

	private void detectClientHost() {
		if (StringUtils.hasText(this.client.getHost())) {
			// 如果配置本机地址，就直接返回
			return;
		}

		if ("127.0.0.1".equals(this.server.getHost())) {
			// 如果服务器是本机，就直接设置client的ip为本机
			this.client.setHost("127.0.0.1");
			return;
		}

		// 本机可能有很多个ip地址，我们是通过找和服务器同一网段的地址来判断哪一个地址才是我们需要的
		List<String> ipList = NetUtil.getAllIpAddress();

		// 尝试根据服务器的ip地址找到本机地址
		String serverHost = this.server.getHost();
		int index = serverHost.indexOf(".");
		if (index > 0) {
			// 如果服务器是用ip，就找本机同网段的的ip
			String prefix = serverHost.substring(0, index + 1);

			for (String ip : ipList) {
				if (ip.startsWith(prefix)) {
					this.client.setHost(ip);
					break;
				}
			}
		}

		if (StringUtils.isEmpty(this.client.getHost())) {
			// 默认先给一个127.0.0.1
			this.client.setHost("127.0.0.1");

			for (String ip : ipList) {
				// 如果有内网地址，就随便给一个
				if (ip.startsWith("192.168") || ip.startsWith("10.")) {
					this.client.setHost(ip);
					break;
				}
			}
		}
	}

	private void detectServerHost() {
		if (StringUtils.hasText(this.server.getHost())) {
			// 如果配置了服务器地址，就返回
			return;
		}
		List<String> ipList = NetUtil.getAllIpAddress();
		for (String ip : ipList) {
			if (ip.startsWith("192.168.100")) {
				// 自动配置林资办公室的服务器
				this.server.setHost("192.168.100.10");
				break;
			}

			if (ip.startsWith("10.")) {
				// 自动配置阿里云的服务器
				this.server.setHost("10.161.178.147");
				break;
			}
		}

		if (StringUtils.isEmpty(server.getHost())) {
			throw new RuntimeException("请配置监控服务器的ip地址，请检查参数monitorClient.server.host");
		}
	}

	@PostConstruct
	protected void init() {

		this.detectServerHost();
		this.detectClientHost();

		log.info("监控系统配置: 本机地址={}:{} , 服务器={}:{}",
				this.client.getHost(), this.client.getPort(),
				this.server.getHost(), this.server.getPort());
	}

}
