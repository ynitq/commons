package com.cfido.commons.spring.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.util.Assert;

import com.cfido.commons.beans.apiExceptions.InvalidVerifyCodeException;
import com.cfido.commons.beans.apiExceptions.MissFieldException;
import com.cfido.commons.beans.apiExceptions.TooBusyException;
import com.cfido.commons.beans.others.CodeVerifyBean;
import com.cfido.commons.spring.debugMode.DebugModeProperties;
import com.cfido.commons.utils.utils.ExceptionUtil;

/**
 * <pre>
 * 基础的校验码服务
 * </pre>
 * 
 * @author 梁韦江 2017年5月25日
 */
public abstract class BaseCodeService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BaseCodeService.class);

	@Autowired
	private RedisTemplate<String, CodeVerifyBean> redisTemplate;

	@Autowired
	private DebugModeProperties debugMode;

	/** 两次发送邮件的时间间隔 */
	private int intervalInSec = 60;

	@ManagedAttribute(description = "验证码的有效期（分钟）")
	public abstract int getCodeExpireTimeInMin();

	@ManagedAttribute(description = "两次发送的最小时间间隔(秒)")
	public int getIntervalInSec() {
		return intervalInSec;
	}

	@ManagedAttribute
	public void setIntervalInSec(int intervalInSec) {
		this.intervalInSec = intervalInSec;
	}

	@ManagedOperation(description = "测试校验验证码")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "key", description = "key"),
			@ManagedOperationParameter(name = "code", description = "验证码"),
	})
	public String jmxCheckCode(String key, String code) throws InvalidVerifyCodeException, MissFieldException {
		ExceptionUtil.isEmail(key, "请输入正确key");
		ExceptionUtil.hasText(code, "验证码不能为空");

		String redisKey = this.createRedisKey(key);

		this.doVerifyCode(redisKey, code, false);

		return "验证通过";
	}

	/**
	 * 验证校验码是否正确
	 * 
	 * @param key
	 *            键值
	 * @param code
	 *            验证码
	 * @param deleteKey
	 *            验证后是否删除
	 * @throws InvalidVerifyCodeException
	 */
	public void verifyCode(String key, String code, boolean deleteKey) throws InvalidVerifyCodeException {
		Assert.hasText(key, "key不能为空");
		Assert.hasText(code, "code不能为空");

		String redisKey = this.createRedisKey(key);
		if (this.debugMode.isDebugMode()) {
			log.warn("开发模式下，自动通过验证 key={} , code={}", redisKey, code);
			return;
		}

		this.doVerifyCode(redisKey, code, deleteKey);
	}

	/**
	 * 正在的执行验证校验码是否正确
	 * 
	 * @param redisKey
	 *            键值
	 * @param code
	 *            验证码
	 * @param deleteKey
	 *            验证后是否删除
	 * @throws InvalidVerifyCodeException
	 */
	private void doVerifyCode(String redisKey, String code, boolean deleteKey) throws InvalidVerifyCodeException {
		Assert.hasText(redisKey, "redisKey不能为空");
		Assert.hasText(code, "code不能为空");

		CodeVerifyBean value = this.redisTemplate.opsForValue().get(redisKey);
		if (value != null) {

			if (code.equals(value.getCode())) {
				log.debug("校验验证码时，找到键值:{} 并验证成功", redisKey);

				// 如果是一次有效的，就删除键值，验证码只能使用一次
				if (deleteKey) {
					this.redisTemplate.delete(redisKey);
				}

				// 验证成功就直接返回，其他情况都是抛错
				return;
			} else {
				log.debug("校验验证码时，找到键值:{}，但验证码不对, 正确值:{}, 传入值:{} ",
						redisKey, value.getCode(), code);
			}
		} else {
			log.debug("校验验证码时，找不到键值:{}", redisKey);
		}
		throw new InvalidVerifyCodeException();
	}

	private String createRedisKey(String key) {
		return String.format("%s:%s", this.getRedisKeyPrefix(), key);
	}

	/**
	 * 获取随机验证码(0-9的整数)
	 */
	private String getRandomCode() {
		int len = this.getRandomCodeLen();
		StringBuilder codeBuilder = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			int value = (int) (Math.random() * 10);
			codeBuilder.append(value);
		}
		return codeBuilder.toString();
	}

	/**
	 * 生成随机码，并且检查发送是否太频繁
	 * 
	 * @param key
	 * @return
	 * @throws TooBusyException
	 *             如果发送太频繁
	 */
	protected String createCodeAndIsTooBusy(String key) throws TooBusyException {

		Assert.hasText(key, "key不能为空");

		String code = this.getRandomCode();

		// 发送验证码前，先将key保存起来
		String redisKey = this.createRedisKey(key);

		CodeVerifyBean oldValue = this.redisTemplate.opsForValue().get(redisKey);
		if (oldValue != null) {
			long now = System.currentTimeMillis();
			long remainInSec = this.intervalInSec - (now - oldValue.getTime()) / 1000;
			if (remainInSec > 0) {
				// 时间小于时间间隔就抛错
				throw new TooBusyException(remainInSec);
			}

		}

		// 将验证码放入redis
		CodeVerifyBean value = new CodeVerifyBean(code);
		this.redisTemplate.opsForValue().set(redisKey, value, getCodeExpireTimeInMin(), TimeUnit.MINUTES);

		return code;
	}

	/** 验证码长度，可有子类通过继承来修改 */
	protected int getRandomCodeLen() {
		return 4;
	}

	/** redis key 前缀 */
	protected abstract String getRedisKeyPrefix();

}
