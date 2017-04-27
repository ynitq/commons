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
@ManagedResource(description = "监控系统客户端服务")
public class MonitorClientService extends CommonMBeanDomainNaming {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MonitorClientService.class);

	@Autowired
	private MonitorClientProperties clientProperties;

	@Autowired
	private MonitorClientContext context;

	private boolean connected = false;

	/** id的json字符串，初始化的时候生成，每次提交数据都需要带上 */
	private String idJsonStr;

	/** 等待5分钟再次重试时用的标志 */
	private final Object retryFlag = new Object();
	private boolean retryQuitFlag = false;

	/**
	 * 异步发送数据的线程池
	 */
	private final BaseThreadPool monitorTreadPool = new BaseThreadPool() {

		@Override
		protected int getUniqueIdSetInitSize() {
			return 0;
		}

		@Override
		protected int getPoolSize() {
			// 单线程就可以了
			return 1;
		}

		@Override
		protected String getName() {
			return "监控系统发送数据线程池";
		}
	};

	/**
	 * <pre>
	 * 用于异步提交数据的任务类
	 * </pre>
	 * 
	 * @author 梁韦江 2016年12月19日
	 */
	private class MyTask implements IMyTask {

		private final String msg;

		private final MonitorMsgTypeEnum level;

		public MyTask(MonitorMsgTypeEnum level, String msg) {
			super();
			this.level = level;
			this.msg = msg;
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

		@Override
		public void afterRun() {
		}

		@Override
		public String getUniqueId() {
			return null;
		}

	}

	@PostConstruct
	protected void init() {

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

	}

	@PreDestroy
	protected void onShutdown() {
		// shutdown的时候，如果重试线程还在跑，就让那个线程退出循环
		this.retryQuitFlag = true;
		synchronized (this.retryFlag) {
			this.retryFlag.notify();
		}
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
			param.put("msg", msg);
		}

		log.debug("向监控服务器 {} 发送信息 {}", serverUrl, msg);

		HttpUtil.request(serverUrl, param, true, null);
	}


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
			log.debug("未和监控服务器联系成功，放弃发送报告，msg={}", msg);
		}
	}

	@ManagedAttribute(description = "是否已经成功向服务器汇报")
	public boolean isConnected() {
		return connected;
	}

	@ManagedAttribute(description = "监控服务器url")
	public String getServerUrl() {
		return this.clientProperties.getServerUrlOfReport();
	}

	@ManagedOperation(description = "测试发送消息给监控服务器")
	@ManagedOperationParameters({
			@ManagedOperationParameter(description = "要发送的信息", name = "msg"),
	})
	public void testSendMsg(String msg) throws IOException {
		this.postMsgToServer(MonitorMsgTypeEnum.WARNING, msg);
	}

	@ManagedAttribute(description = "监控服务器Host")
	public String getServerHost() {
		return this.clientProperties.getServer().getHost();
	}

	@ManagedAttribute(description = "主程序的生成时间")
	public String getStartClassBuildTime() {
		Date date = this.context.getStartClassBuildTime();
		if (date != null) {
			return DateUtil.dateFormat(date);
		} else {
			return "获取文件生成时间失败";
		}
	}

	@ManagedAttribute()
	public void setServerHost(String serverHost) {
		this.clientProperties.getServer().setHost(serverHost);
	}

	@ManagedAttribute(description = "本机监控id")
	public String getIdJsonStr() {
		return idJsonStr;
	}

	@ManagedAttribute(description = "是否汇报给监控服务器")
	public boolean isEnable() {
		return this.clientProperties.isEnable();
	}

	@ManagedAttribute()
	public void setEnable(boolean enable) {
		this.clientProperties.setEnable(enable);
	}

}
