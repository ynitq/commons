package com.cfido.commons.spring.monitor;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.cfido.commons.beans.monitor.ClientInfoResponse;
import com.cfido.commons.beans.monitor.ClientMsgForm;
import com.cfido.commons.beans.monitor.ServerRightsBean;
import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.jmxInWeb.core.JmxInWebService;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;
import com.cfido.commons.utils.threadPool.BaseThreadPool;
import com.cfido.commons.utils.threadPool.IMyTask;
import com.cfido.commons.utils.utils.DateUtil;
import com.cfido.commons.utils.utils.HttpUtil;
import com.cfido.commons.utils.utils.LogUtil;

/**
 * <pre>
 * 监控客户端的服务程序，主要功能是定时发送信息给服务器
 * </pre>
 * 
 * @author 梁韦江 2016年12月16日
 */
@Service
public class MonitorClientService {

	@ManagedResource(description = "监控系统客户端服务")
	@ADomainOrder(order = CommonMBeanDomainNaming.ORDER, domainName = CommonMBeanDomainNaming.DOMAIN)
	public class MonitorClientMBean {

		@ManagedAttribute(description = "本机监控id")
		public String getIdJsonStr() {
			return idJsonStr;
		}

		@ManagedAttribute(description = "监控服务器Host")
		public String getServerHost() {
			return MonitorClientService.this.clientProperties.getServer().getHost();
		}

		@ManagedAttribute(description = "监控服务器url")
		public String getServerUrl() {
			return MonitorClientService.this.clientProperties.getServerUrlOfReport();
		}

		@ManagedAttribute(description = "主程序的生成时间")
		public String getStartClassBuildTime() {
			Date date = MonitorClientService.this.context.getStartClassBuildTime();
			if (date != null) {
				return DateUtil.dateFormat(date);
			} else {
				return "获取文件生成时间失败";
			}
		}

		@ManagedAttribute(description = "是否已经成功向服务器汇报")
		public boolean isConnected() {
			return connected;
		}

		@ManagedAttribute(description = "是否汇报给监控服务器")
		public boolean isEnable() {
			return MonitorClientService.this.clientProperties.isEnable();
		}

		@ManagedAttribute()
		public void setEnable(boolean enable) {
			MonitorClientService.this.clientProperties.setEnable(enable);
		}

		@ManagedAttribute()
		public void setServerHost(String serverHost) {
			MonitorClientService.this.clientProperties.getServer().setHost(serverHost);
		}

		@ManagedOperation(description = "测试发送消息给监控服务器")
		@ManagedOperationParameters({
				@ManagedOperationParameter(description = "要发送的信息", name = "msg"),
		})
		public void testSendMsg(String msg) throws IOException {
			MonitorClientService.this.postMsgToServer(MonitorMsgTypeEnum.WARNING, msg);
		}
	}

	/**
	 * <pre>
	 * 用于异步提交数据的任务类
	 * </pre>
	 * 
	 * @author 梁韦江 2016年12月19日
	 */
	private class MyTask implements IMyTask {

		private final MonitorMsgTypeEnum level;

		private final String msg;

		public MyTask(MonitorMsgTypeEnum level, String msg) {
			super();
			this.level = level;
			this.msg = msg;
		}

		@Override
		public String getUniqueId() {
			return null;
		}

		@Override
		public void run() {
			try {
				MonitorClientService.this.postMsgToServer(level, msg);
			} catch (IOException ex) {
				LogUtil.traceError(log, ex, "发送数据给监控服务器时发生错误，url=" +
						MonitorClientService.this.clientProperties.getServerUrlOfReport());
			}
		}

	}

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MonitorClientService.class);

	@Autowired
	private MonitorClientProperties clientProperties;

	private boolean connected = false;

	@Autowired
	private MonitorClientContext context;

	@Autowired
	private JmxInWebService jmxInWebService;

	/** id的json字符串，初始化的时候生成，每次提交数据都需要带上 */
	private String idJsonStr;

	/**
	 * 异步发送数据的线程池
	 */
	private BaseThreadPool monitorTreadPool;

	/** 等待5分钟再次重试时用的标志 */
	private final Object retryFlag = new Object();

	private boolean retryQuitFlag = false;

	/**
	 * 向服务器汇报一些消息
	 * 
	 * @param level
	 *            信息的等级
	 * @param msg
	 *            要汇报的消息
	 */
	public void reportMsgToServer(MonitorMsgTypeEnum level, String msg) {
		if (connected) {
			if (StringUtils.hasText(msg)) {
				MyTask task = new MyTask(level, msg);
				// 将任务放到线程池，异步发送数据
				this.monitorTreadPool.addNewTask(task);
			}
		} else {
			log.warn("未和监控服务器联系成功，放弃发送报告，msg={}", msg);
		}
	}

	/**
	 * 向服务器报告一下id。 服务器对应的表单时 {@link ClientMsgForm}
	 */
	private void postMsgToServer(MonitorMsgTypeEnum level, String msg) throws IOException {
		String serverUrl = this.clientProperties.getServerUrlOfReport();

		ClientInfoResponse clientInfo = this.context.getClientInfo(false);

		Map<String, Object> param = new HashMap<>();
		param.put("idStr", idJsonStr);
		param.put("clientInfo", JSON.toJSONString(clientInfo));
		param.put("msgType", level.code);

		if (StringUtils.hasText(msg)) {
			// 如果有消息，就传消息过去
			param.put("msg", msg);
		} else {
			// 如果没有消息，就表示只在想中心服务器注册一下，这个时候，就将权限定义也传过去
			ServerRightsBean rights = this.context.getRightsDef();
			if (rights != null) {
				param.put("rightStr", JSON.toJSON(rights));
			}
		}

		log.info("向监控服务器 {} 发送信息 {}", serverUrl, msg);

		HttpUtil.request(serverUrl, param, true, null);
	}

	private void reportToServer() throws InterruptedException {

		if (!this.clientProperties.isEnable()) {
			return;
		}

		while (!retryQuitFlag) {
			try {
				this.postMsgToServer(MonitorMsgTypeEnum.START, null);
				this.connected = true;
				log.info("成功向监控服务器端报告了本程序启动");
				break;
			} catch (IOException e) {
				if (this.clientProperties.getReport().isRetryWhenFail()) {
					// 如果配置了重试
					long min = this.clientProperties.getReport().getRetryDelay();

					log.info("向监控服务器报告失败， {}分钟后再次重试，监控服务器:{}",
							min,
							this.clientProperties.getServerUrlOfReport());

					synchronized (retryFlag) {
						// 等待5分钟
						this.retryFlag.wait(TimeUnit.MINUTES.toMillis(min));
					}
				} else {
					log.info("向监控服务器报告失败， 但不再重试了");
				}
			}
		}
	}

	@PostConstruct
	protected void init() {

		if (this.clientProperties.isEnable()) {

			this.monitorTreadPool = new BaseThreadPool() {

				@Override
				protected String getName() {
					return "监控系统发送数据线程池";
				}

				@Override
				protected int getPoolSize() {
					// 单线程就可以了
					return 1;
				}

				@Override
				protected int getUniqueIdSetInitSize() {
					return 0;
				}
			};

			// 注册MBean
			this.jmxInWebService.register(new MonitorClientMBean());

			this.connected = false;

			// id的json字符串
			this.idJsonStr = JSON.toJSONString(this.context.getClientId(), true);

			// 启动报告线程
			Thread reportThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						MonitorClientService.this.reportToServer();
					} catch (InterruptedException e) {
					}
				}
			}, "监控客户端报告线程");

			reportThread.start();

		} else {
			log.warn("监控客户端 已经被禁用");
		}

	}

	@PreDestroy
	protected void onShutdown() {
		// shutdown的时候，如果重试线程还在跑，就让那个线程退出循环
		this.retryQuitFlag = true;
		synchronized (this.retryFlag) {
			this.retryFlag.notify();
		}
	}

}
