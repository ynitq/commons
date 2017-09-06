package com.cfido.commons.spring.monitor;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.cfido.commons.beans.monitor.ClientIdBean;
import com.cfido.commons.beans.monitor.ClientInfoResponse;
import com.cfido.commons.beans.monitor.DiskInfoBean;
import com.cfido.commons.beans.monitor.OsInfoBean;
import com.cfido.commons.utils.utils.ClassUtil;
import com.cfido.commons.utils.utils.OperatingSystemUtil;

/**
 * <pre>
 * 客户端Context
 * </pre>
 * 
 * @author 梁韦江 2016年12月16日
 */
@Service
public class MonitorClientContext {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MonitorClientContext.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ServerProperties serverProperties;

	@Autowired
	private MonitorClientProperties clientProperties;

	@Autowired(required = false)
	private IMonitorClientExInfoProvider exInfoProvider;

	private final ClientIdBean clientId = new ClientIdBean();

	private final AtomicInteger requestCounter = new AtomicInteger();

	private Date startClassBuildTime;

	private final long startTime = System.currentTimeMillis();

	@PostConstruct
	protected void init() throws IOException {
		// 设置启动程序名
		String startClassName = this.getStartClassName();
		this.clientId.setStartClassName(startClassName);

		// 获取主程序的生成时间
		this.startClassBuildTime = ClassUtil.getClassBuildTime(startClassName);

		// 设置 本机ip
		this.clientId.setHost(this.clientProperties.getClient().getHost());

		this.clientId.setContextPath(this.serverProperties.getContextPath());

		// 配置启动程序的端口
		if (this.clientProperties.getClient().getPort() == 0) {
			// 如果配置文件中没有配置客户端的端口，就直接从Spring boot的配置中获取端口
			this.clientId.setPort(this.getMyPort());
		} else {
			// 如果有配置，就用配置的端口
			this.clientId.setPort(this.clientProperties.getClient().getPort());
		}

		log.debug("初始化监控客户端 {} 监控服务器={}, 额外信息={}",
				this.clientId.toString(),
				this.clientProperties.getServerUrlOfReport(),
				this.exInfoProvider != null);

	}

	private int getMyPort() {
		Integer port = this.serverProperties.getPort();
		if (port != null) {
			return port.intValue();
		} else {
			return 8080;
		}
	}

	/**
	 * 获得启动程序的类名
	 */
	private String getStartClassName() {
		// 我们在主程序上放了注解，所以可通过注解找到主程序
		Map<String, Object> map = this.applicationContext.getBeansWithAnnotation(SpringBootApplication.class);

		if (map.isEmpty()) {
			throw new RuntimeException("主程序必须有 @SpringBootApplication 的注解");
		}

		if (map.size() > 1) {
			throw new RuntimeException("超过一个程序有 @SpringBootApplication 的注解，请只主程序上放@SpringBootApplication");
		}

		// 获取的类名一般是代理类，例如com.linzi.entInvsgt.StartEntInvsgt$$EnhancerBySpringCGLIB$$7de1e828@7b4e7a17
		String startClassName = map.values().toArray()[0].toString();

		// 需要从代理类的类名中分析出真正的类名
		int index = startClassName.indexOf("$$");
		if (index > 0) {
			return startClassName.substring(0, index);
		} else {
			return startClassName;
		}
	}

	/**
	 * 获取客户端id，用于启动时上报给监控服务器
	 */
	public ClientIdBean getClientId() {
		return clientId;
	}

	/**
	 * 获取当前系统信息
	 */
	public ClientInfoResponse getClientInfo(boolean resetRequestCounter) {
		ClientInfoResponse res = new ClientInfoResponse();

		res.setRequestCount(this.requestCounter.get());// 这段时间发生的请求的数量
		if (resetRequestCounter) {
			// 如果要重置计数器
			this.requestCounter.set(0);// 清空计数器
		}

		res.setStartTime(startTime);// 系统启动的时间
		res.setCreateTime(System.currentTimeMillis());// 信息的生成时间
		res.setDiskInfo(getDistInfo());// 硬盘信息
		res.setOsInfo(getOsInfo());// 操作系统信息

		// 主程序的生成时间
		if (this.startClassBuildTime != null) {
			res.setStartClassBuildTime(this.startClassBuildTime.getTime());
		}

		if (this.exInfoProvider != null) {
			// 如果需要提供额外的信息，就更新一下
			this.exInfoProvider.updateExInfo(res, resetRequestCounter);
		}

		return res;
	}

	/**
	 * 获得操作系统的信息
	 */
	private OsInfoBean getOsInfo() {
		OsInfoBean bean = new OsInfoBean();
		bean.setFreeMemory(OperatingSystemUtil.getFreeMemory());
		bean.setMaxMemory(OperatingSystemUtil.getMaxMemory());
		bean.setSystemLoadAverage(OperatingSystemUtil.getSystemLoadAverage());
		bean.setTotalMemory(OperatingSystemUtil.getTotalMemory());
		bean.setUsedMemory(OperatingSystemUtil.getUsedMemory());
		return bean;
	}

	/**
	 * 获取硬盘信息
	 */
	private DiskInfoBean getDistInfo() {
		File file = new File(".");

		DiskInfoBean bean = new DiskInfoBean();
		bean.setFreeSpace(file.getFreeSpace());
		bean.setTotalSpace(file.getTotalSpace());
		bean.setUsableSpace(file.getUsableSpace());

		return bean;
	}

	/**
	 * 访问次数+1
	 */
	public void addRequest() {
		log.debug("监控系统监控到访问次数 +1");
		this.requestCounter.incrementAndGet();
	}

	public Date getStartClassBuildTime() {
		return startClassBuildTime;
	}

}
