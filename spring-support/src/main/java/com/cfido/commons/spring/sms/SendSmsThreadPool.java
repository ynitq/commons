package com.cfido.commons.spring.sms;

import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;
import com.cfido.commons.utils.threadPool.BaseThreadPool;

/**
 * <pre>
 * 异步操作线程池
 * </pre>
 * 
 * @author 梁韦江 2015年7月21日
 */
@Service
@ManagedResource(description = "短信发送异步线程池", objectName = "SmsCodeService:name=短信验证码-线程池")
@ADomainOrder(order = CommonMBeanDomainNaming.ORDER, domainName = CommonMBeanDomainNaming.DOMAIN_SMS)
public class SendSmsThreadPool extends BaseThreadPool {

	@Override
	protected String getName() {
		return "短信发送异步线程池";
	}

	@Override
	protected int getPoolSize() {
		// 异步操作可能是非常慢的任务，可以将线程池的数量调大点
		return 2;
	}

	@Override
	protected int getUniqueIdSetInitSize() {
		return 1000;
	}
}
