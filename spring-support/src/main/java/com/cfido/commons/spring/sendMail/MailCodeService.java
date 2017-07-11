package com.cfido.commons.spring.sendMail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.cfido.commons.beans.apiExceptions.InvalidVerifyCodeException;
import com.cfido.commons.beans.apiExceptions.MissFieldException;
import com.cfido.commons.beans.apiExceptions.TooBusyException;
import com.cfido.commons.spring.dict.core.DictCoreService;
import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.utils.BaseCodeService;
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
public class MailCodeService extends BaseCodeService {

	private static final String TEST_MAIL_TYPE = "系统测试邮件";

	@Autowired
	private SendMailService sendMailService;

	@Autowired
	private DictCoreService dictCoreService;

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
	 * @throws TooBusyException
	 * @throws MissFieldException
	 */
	public void sendCode(String mailType, String email, String subject, String text, String code)
			throws TooBusyException, MissFieldException {

		ExceptionUtil.isEmail(email, "请输入正确email");
		Assert.hasText(mailType, "mailType不能为空");
		Assert.hasText(subject, "subject不能为空");
		Assert.hasText(text, "text不能为空");
		Assert.hasText(code, "code不能为空");

		String key = this.createKey(mailType, email);

		// 检查是否太频繁
		this.checkTooBusy(key);

		// 将验证码保存下来
		this.saveCode(key, code);

		// 用系统名字作为邮件标题发送邮件
		this.sendMailService.sendMail(this.dictCoreService.getSystemName(),
				email, subject,
				"<html><body>" + text + "</body></html>");
	}

	private String createKey(String mailType, String email) {
		return String.format("%s:%s", email, mailType);
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
		this.checkRandCode(key, code);
	}

	@Override
	@ManagedAttribute(description = "验证码的有效期（分钟）")
	public int getCodeExpireTimeInMin() {
		return codeExpireTimeInMin;
	}

	@ManagedAttribute
	public void setCodeExpireTimeInMin(int codeExpireTimeInMin) {
		this.codeExpireTimeInMin = codeExpireTimeInMin;
	}

	@Override
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
	public void testSendCode(String email, String code) throws TooBusyException, MissFieldException {
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

		String key = this.createKey(TEST_MAIL_TYPE, email);
		this.doVerifyCode(key, code, true);
	}

	@Override
	protected String getRedisKeyPrefix() {
		return "mailCode";
	}

}
