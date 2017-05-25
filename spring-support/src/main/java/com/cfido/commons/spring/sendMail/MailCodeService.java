package com.cfido.commons.spring.sendMail;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.cfido.commons.beans.apiExceptions.InvalidVerifyCodeException;
import com.cfido.commons.beans.apiExceptions.MissFieldException;
import com.cfido.commons.beans.apiExceptions.TooBusyWhenSendMailException;
import com.cfido.commons.beans.others.CodeVerifyBean;
import com.cfido.commons.spring.debugMode.DebugModeProperties;
import com.cfido.commons.spring.dict.core.DictCoreService;
import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;
import com.cfido.commons.utils.utils.ExceptionUtil;

/**
 * <pre>
 * 邮箱验证服务
 * </pre>
 * 
 * @author 梁韦江 2017年5月25日
 */
@ManagedResource(description = "邮件验证码服务")
@Service
@ADomainOrder(order = CommonMBeanDomainNaming.ORDER, domainName = CommonMBeanDomainNaming.DOMAIN_MAIL)
public class MailCodeService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MailCodeService.class);

	private static final String TEST_MAIL_TYPE = "系统测试邮件";

	@Autowired
	private RedisTemplate<String, CodeVerifyBean> redisTemplate;

	@Autowired
	private SendMailService sendMailService;

	@Autowired
	private DictCoreService dictCoreService;

	@Autowired
	private DebugModeProperties debugMode;

	/** 验证码的有效期 */
	private int codeExpireTimeInMin = 10;

	/** 两次发送邮件的时间间隔 */
	private int intervalInSec = 60;

	/**
	 * 通过邮件发送验证码
	 * 
	 * @param mailType
	 *            类型
	 * @param email
	 *            收件人
	 * @param subject
	 *            标题
	 * @param text
	 *            正文
	 * @param code
	 *            验证码
	 * @throws TooBusyWhenSendMailException
	 * @throws MissFieldException
	 */
	public void sendCode(String mailType, String email, String subject, String text, String code)
			throws TooBusyWhenSendMailException, MissFieldException {

		ExceptionUtil.isEmail(email, "请输入正确email");
		Assert.hasText(mailType, "mailType不能为空");
		Assert.hasText(subject, "subject不能为空");
		Assert.hasText(text, "text不能为空");
		Assert.hasText(code, "code不能为空");

		// 发送验证码邮件前，先将key保存起来
		String key = this.createKey(mailType, email);

		CodeVerifyBean oldValue = this.redisTemplate.opsForValue().get(key);
		if (oldValue != null) {
			long now = System.currentTimeMillis();
			if (now - oldValue.getTime() < TimeUnit.SECONDS.toMillis(intervalInSec)) {
				// 时间小于时间间隔就抛错
				throw new TooBusyWhenSendMailException();
			}

		}

		CodeVerifyBean value = new CodeVerifyBean(code);
		this.redisTemplate.opsForValue().set(key, value, codeExpireTimeInMin, TimeUnit.MINUTES);

		// 用系统名字作为邮件标题发送邮件
		this.sendMailService.sendMail(this.dictCoreService.getSystemName(),
				email, subject,
				"<html><body>" + text + "</body></html>");
	}

	private String createKey(String mailType, String email) {
		return String.format("mailCode:%s——%s", email, mailType);
	}

	/**
	 * 验证校验码是否正确
	 * 
	 * @throws MissFieldException
	 */
	public void verifyCode(String mailType, String email, String code) throws InvalidVerifyCodeException, MissFieldException {
		ExceptionUtil.isEmail(email, "请输入正确email");
		Assert.hasText(mailType, "mailType不能为空");
		Assert.hasText(code, "code不能为空");

		String key = this.createKey(mailType, email);
		if (this.debugMode.isDebugMode() && !TEST_MAIL_TYPE.equals(mailType)) {
			log.warn("开发模式下，自动通过验证 key={} , code={}", key, code);
			return;
		}

		CodeVerifyBean value = this.redisTemplate.opsForValue().get(key);
		if (value != null) {

			if (code.equals(value.getCode())) {
				log.debug("校验验证码时，找到键值:{} 并验证成功", key);

				// 验证成功就删除键值，验证码只能使用一次
				this.redisTemplate.delete(key);

				// 验证成功就直接返回，其他情况都是抛错
				return;
			} else {
				log.debug("校验验证码时，找到键值:{}，但验证码不对, 正确值:{}, 传入值:{} ",
						key, value.getCode(), code);
			}
		} else {
			log.debug("校验验证码时，找不到键值:{}", key);
		}
		throw new InvalidVerifyCodeException();
	}

	@ManagedAttribute(description = "验证码的有效期（分钟）")
	public int getCodeExpireTimeInMin() {
		return codeExpireTimeInMin;
	}

	@ManagedAttribute
	public void setCodeExpireTimeInMin(int codeExpireTimeInMin) {
		this.codeExpireTimeInMin = codeExpireTimeInMin;
	}

	@ManagedAttribute(description = "两次发送的最小时间间隔(秒)")
	public int getIntervalInSec() {
		return intervalInSec;
	}

	@ManagedAttribute
	public void setIntervalInSec(int intervalInSec) {
		this.intervalInSec = intervalInSec;
	}

	@ManagedOperation(description = "测试发送邮件")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "email", description = "email"),
			@ManagedOperationParameter(name = "code", description = "验证码"),
	})
	public void testSendCode(String email, String code) throws TooBusyWhenSendMailException, MissFieldException {
		ExceptionUtil.isEmail(email, "请输入正确email");
		ExceptionUtil.hasText(code, "验证码不能为空");
		this.sendCode(TEST_MAIL_TYPE, email, "测试标题", "<html><head></head><body>测试正文</body><html>", code);
	}

	@ManagedOperation(description = "测试校验验证码")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "email", description = "email"),
			@ManagedOperationParameter(name = "code", description = "验证码"),
	})
	public void testCheckCode(String email, String code) throws InvalidVerifyCodeException, MissFieldException {
		ExceptionUtil.isEmail(email, "请输入正确email");
		ExceptionUtil.hasText(code, "验证码不能为空");

		this.verifyCode(TEST_MAIL_TYPE, email, code);
	}

}
