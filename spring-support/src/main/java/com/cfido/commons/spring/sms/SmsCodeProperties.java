package com.cfido.commons.spring.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;

/**
 * <pre>
 * sms相关配置参数
 *
 * 默认参数：
 * sms.limit.interval=60
 * sms.limit.preIp = 1000
 * sms.limit.preDay = 2000
 * </pre>
 * 
 * @author 梁韦江 2016年8月11日
 */
@ManagedResource(description = "短信服务限制参数")
@ConfigurationProperties(prefix = "sms.limit")
@ADomainOrder(order = CommonMBeanDomainNaming.ORDER, domainName = CommonMBeanDomainNaming.DOMAIN_SMS)
public class SmsCodeProperties {

	private int interval = 60;

	/** 每个IP每天发送的上限 sms.dayLimit.preIp 默认1000 */
	private int preIp = 1000;

	/** 每天可发送的上限 sms.dayLimit.total 默认2000 */
	private int preDay = 2000;

	/**
	 * sms.interval 两次发送短信的实际间隔，默认60秒
	 * 
	 * @return
	 */
	@ManagedAttribute(description = "sms.limit.interval 两次发送短信的实际间隔（秒）")
	public int getInterval() {
		return interval;
	}

	@ManagedAttribute()
	public void setInterval(int interval) {
		this.interval = interval;
	}

	@ManagedAttribute(description = "sms.limit.preIp 每个IP每天发送的上限 ")
	public int getPreIp() {
		return preIp;
	}

	@ManagedAttribute
	public void setPreIp(int dayLimitPreIp) {
		this.preIp = dayLimitPreIp;
	}

	@ManagedAttribute(description = "sms.limit.preDay 每天可发送的上限 ")
	public int getPreDay() {
		return preDay;
	}

	@ManagedAttribute
	public void setPreDay(int preDay) {
		this.preDay = preDay;
	}
}
