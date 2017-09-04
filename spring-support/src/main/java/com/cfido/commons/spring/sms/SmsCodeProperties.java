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

	/** 每个IP每天发送的上限 sms.dayLimit.preIp 默认100 */
	private int preIp = 100;

	/** 每天可发送的上限 sms.dayLimit.total 默认20000 */
	private int preDay = 20000;

	/** 验证码超时时间 */
	private int expireInMin = 5;

	/** 验证码长度 */
	private int codeLen = 4;

	@ManagedAttribute(description = "sms.limit.expireInMin 短信有效时间（分钟）")
	public int getExpireInMin() {
		return expireInMin;
	}

	@ManagedAttribute(description = "sms.limit.codeLen 验证码长度）")
	public int getCodeLen() {
		return codeLen;
	}

	@ManagedAttribute
	public void setCodeLen(int codeLen) {
		this.codeLen = codeLen;
	}

	@ManagedAttribute()
	public void setExpireInMin(int expireInMin) {
		this.expireInMin = expireInMin;
	}

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
