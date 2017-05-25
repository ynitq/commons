package com.cfido.commons.spring.sendMail;

import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;
import com.cfido.commons.utils.threadPool.BaseThreadPool;

/**
 * <pre>
 * 发邮件的线程池
 * </pre>
 * 
 * @author 梁韦江 2015年7月21日
 */
@Service
@ManagedResource(description = "发邮件线程池")
@ADomainOrder(order = CommonMBeanDomainNaming.ORDER, domainName = CommonMBeanDomainNaming.DOMAIN_MAIL)
public class SendMailThreadPool extends BaseThreadPool {

	@Override
	protected String getName() {
		return "发送邮件线程池";
	}

	@Override
	protected int getPoolSize() {
		// 发邮件是非常慢的任务，可以将线程池的数量调大点
		return 5;
	}

	@Override
	protected int getUniqueIdSetInitSize() {
		return 1000;
	}

}
