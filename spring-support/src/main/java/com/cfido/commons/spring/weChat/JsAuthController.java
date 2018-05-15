package com.cfido.commons.spring.weChat;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;

/**
 * <pre>
 * 用于接受微信网页授权，并将code转发给其他应用。
 * 注意：这些被转发的其他应用的微信appId必须和本代理程序的appId一样
 * </pre>
 */
@Controller
@ManagedResource(description = "认证微信js接口安全域名")
@ADomainOrder(domainName = CommonMBeanDomainNaming.DOMAIN_WECHAT, order = CommonMBeanDomainNaming.ORDER)
@ConditionalOnProperty(prefix = "wechat", name = "jsAuth", havingValue = "true", matchIfMissing = false) // 如果enable=true开开启本服务
public class JsAuthController {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JsAuthController.class);

	private final static String VERIFY_CODE_KEY = "weixin:verify";

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@ManagedAttribute(description = "JS接口安全域名验证码。文件MP_verify_*.txt中的内容")
	public String getVerifyCode() {
		return this.redisTemplate.opsForValue().get(VERIFY_CODE_KEY);
	}

	@ManagedAttribute
	public void setVerifyCode(String verifyCode) {
		this.redisTemplate.opsForValue().set(VERIFY_CODE_KEY, verifyCode);
	}

	@RequestMapping("/MP_verify_*.txt")
	@ResponseBody
	public String verifyCode() {
		return this.getVerifyCode();
	}

	@PostConstruct
	protected void init() {
		log.info("激活JS接口安全域名验证功能，请在JMX中管理");
	}

}
