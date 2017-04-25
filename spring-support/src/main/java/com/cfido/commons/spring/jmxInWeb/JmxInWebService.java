package com.cfido.commons.spring.jmxInWeb;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * MBean服务，可注册mbean，但spring boot都是自动的，所以基本上啥都不用做
 * 
 * 可通过 {@link MBeanExporter#setNamingStrategy(org.springframework.jmx.export.naming.ObjectNamingStrategy)} 改变名字获取规则
 * </pre>
 * 
 * @author 梁韦江 2016年8月11日
 */
@Service
public class JmxInWebService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JmxInWebService.class);

	@Autowired
	private MBeanExporter mBeanExporter;

	/** 在init() 中初始化 */
	private MBeanServer server;

	/**
	 * 注册一个mbean，默认的domain是obj的包名，名字是类名
	 * 
	 * @param obj
	 */
	public void register(Object obj) {
		if (obj != null) {
			Class<?> clazz = obj.getClass();
			this.register(obj, clazz.getPackage().getName(), clazz.getSimpleName());
		}
	}

	/**
	 * 注册一个mbean，默认名字是类名
	 * 
	 * @param obj
	 */
	public void register(Object obj, String domain) {
		if (obj != null) {
			Class<?> clazz = obj.getClass();
			this.register(obj, domain, clazz.getSimpleName());
		}
	}

	/**
	 * 注册一个mbean
	 * 
	 * @param obj
	 *            mbean的对象
	 * @param domain
	 *            分类名
	 * @param name
	 *            mbean名
	 */
	public void register(Object obj, String domain, String name) {

		try {
			ObjectName oname = ObjectName.getInstance(domain + ":name=" + name);
			if (!this.server.isRegistered(oname)) {
				this.mBeanExporter.registerManagedResource(obj, oname);
			}
		} catch (Exception e) {
			log.error("jmx注册失败", e);
		}
	}

	@PostConstruct
	protected void init() throws IOException {
		this.server = this.mBeanExporter.getServer();
	}

	public void unRegister(Object obj) {
		if (obj != null) {
			Class<?> clazz = obj.getClass();
			this.unRegister(clazz.getPackage().getName(), clazz.getSimpleName());
		}
	}

	/**
	 * 反注册一个mbean
	 * 
	 * @param domain
	 *            分类名
	 * @param name
	 *            mbean名
	 */
	public void unRegister(String domain, String name) {
		try {
			ObjectName oname = ObjectName.getInstance(domain + ":name=" + name);
			if (this.server.isRegistered(oname)) {
				this.server.unregisterMBean(oname);
			}
		} catch (Exception e) {
			log.error("jmx注销注册失败", e);
		}
	}

}
