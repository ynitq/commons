package com.cfido.commons.spring.utils;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.util.Assert;

import com.cfido.commons.beans.apiExceptions.InvalidVerifyCodeException;
import com.cfido.commons.beans.apiExceptions.TooBusyException;
import com.cfido.commons.beans.others.CodeVerifyBean;

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

	/**
	 * 验证校验码是否正确, 默认是一次性验证
	 * 
	 * @param key
	 *            键值
	 * @param code
	 *            验证码
	 * @throws InvalidVerifyCodeException
	 */
	public void checkRandCode(String key, String code) throws InvalidVerifyCodeException {
		this.checkRandCode(key, code, true);
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
	public void checkRandCode(String key, String code, boolean deleteKey) throws InvalidVerifyCodeException {
		Assert.hasText(key, "key不能为空");
		Assert.hasText(code, "code不能为空");

		this.doVerifyCode(key, code, deleteKey);
	}

	/**
	 * 生成随机码，并且检查发送是否太频繁
	 * 
	 * @param key
	 * @return
	 * @throws TooBusyException
	 *             如果发送太频繁
	 */
	public String createRandomCodeAndCheckBusy(String key) throws TooBusyException {
		// 检查操作太频繁
		this.checkTooBusy(key);

		// 生成验证码
		String code = this.getRandomCode();

		// 保存验证码
		this.saveCode(key, code);

		// 返回验证码
		return code;
	}

	@ManagedAttribute(description = "验证码校验模式")
	public String getCheckMode() {
		if (this.isDebug()) {
			return "当前处于开发模式:不做校验，自动通过";
		} else {
			return "当前处于运行模式：需要校验";
		}
	}

	@ManagedAttribute(description = "验证码的有效期（分钟）")
	public abstract int getCodeExpireTimeInMin();

	@ManagedAttribute(description = "两次发送的最小时间间隔(秒)")
	public abstract int getIntervalInSec();

	/** 是否debug **/
	public abstract boolean isDebug();

	/** 是否真的发送 **/
	public abstract boolean isSend();

	/** 验证码长度，可有子类通过继承来修改 */
	@ManagedAttribute(description = "验证码的长度")
	protected int getRandomCodeLen() {
		return 4;
	}

	/** 将code保存到redis中 */
	public String saveCode(String key, String code) {
		Assert.hasText(key, "key不能为空");

		String redisKey = this.createRedisKey(key);

		// 将验证码放入redis
		CodeVerifyBean value = new CodeVerifyBean(code);
		this.redisTemplate.opsForValue().set(redisKey, value, getCodeExpireTimeInMin(), TimeUnit.MINUTES);

		return code;
	}

	private String createRedisKey(String key) {
		return String.format("%s:%s", this.getRedisKeyPrefix(), key);
	}

	/** 检查操作是否太频繁 */
	protected void checkTooBusy(String key) throws TooBusyException {
		Assert.hasText(key, "key不能为空");

		// 发送验证码前，先将key保存起来
		String redisKey = this.createRedisKey(key);

		CodeVerifyBean oldValue = this.redisTemplate.opsForValue().get(redisKey);
		int intervalInSec = this.getIntervalInSec();
		if (oldValue != null && intervalInSec > 0) {
			long now = System.currentTimeMillis();
			long remainInSec = intervalInSec - (now - oldValue.getTime()) / 1000;
			if (remainInSec > 0) {
				// 时间小于时间间隔就抛错
				throw new TooBusyException(remainInSec);
			}
		}
	}

	/**
	 * 正在的执行验证校验码是否正确
	 * 
	 * @param key
	 *            键值
	 * @param code
	 *            验证码
	 * @param deleteKey
	 *            验证后是否删除
	 * @throws InvalidVerifyCodeException
	 */
	protected void doVerifyCode(String key, String code, boolean deleteKey) throws InvalidVerifyCodeException {
		Assert.hasText(key, "redisKey不能为空");
		Assert.hasText(code, "code不能为空");

		String redisKey = this.createRedisKey(key);

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

		if (this.isDebug()) {
			log.warn("验证码不正确，但开发模式下，也自动当验证通过 key={} , code={}", key, code);
		} else {
			throw new InvalidVerifyCodeException();
		}
	}

	protected void deleteCodeKey(String key) {
		String redisKey = this.createRedisKey(key);
		this.redisTemplate.delete(redisKey);
	}

	/**
	 * 获取随机验证码(0-9的整数)
	 */
	protected String getRandomCode() {
		int len = this.getRandomCodeLen();
		StringBuilder codeBuilder = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			int value = (int) (Math.random() * 10);
			codeBuilder.append(value);
		}
		return codeBuilder.toString();
	}

	/** redis key 前缀 */
	protected abstract String getRedisKeyPrefix();

}
