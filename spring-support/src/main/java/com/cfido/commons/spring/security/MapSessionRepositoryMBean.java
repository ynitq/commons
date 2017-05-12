package com.cfido.commons.spring.security;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.session.ExpiringSession;

import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;
import com.cfido.commons.utils.utils.LRULinkedHashMap;

/**
 * <pre>
 * 用于查看MapSessionRepository的MBean
 * </pre>
 * 
 * @author 梁韦江 2017年5月12日
 */
@ManagedResource(description = "用于查看MapSessionRepository的MBean")
@ADomainOrder(order = CommonMBeanDomainNaming.ORDER, domainName = CommonMBeanDomainNaming.DOMAIN)
public class MapSessionRepositoryMBean {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MapSessionRepositoryMBean.class);

	private final LRULinkedHashMap<String, ExpiringSession> map;

	private final Timer timer = new Timer("MapSessionRepository维护线程", true);

	public MapSessionRepositoryMBean(LRULinkedHashMap<String, ExpiringSession> map) {
		super();
		this.map = map;

		// 每个小时维护一次
		this.timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				MapSessionRepositoryMBean.this.clearExpired();
			}
		}, 0, TimeUnit.HOURS.toMillis(1));
	}

	@ManagedOperation(description = "立刻清理过期Session")
	public void clearExpired() {
		int count = 0;

		// 先获取所有的内容
		List<Map.Entry<String, ExpiringSession>> list = new LinkedList<>();
		this.map.getLock().lock();
		try {
			list.addAll(this.map.entrySet());
		} finally {
			this.map.getLock().unlock();
		}

		// 删除所有超时的session
		for (Map.Entry<String, ExpiringSession> en : list) {
			if (en.getValue().isExpired()) {
				count++;
				map.remove(en.getKey());
			}
		}

		log.debug("MapSessionRepository维护线程 清理了{} 条过期的session", count);
	}

	@ManagedAttribute(description = "map的最大容量")
	public int getMaxCapacity() {
		return this.map.getMaxCapacity();
	}

	@ManagedAttribute(description = "map已经使用的容量")
	public int getSize() {
		return this.map.size();
	}
}
