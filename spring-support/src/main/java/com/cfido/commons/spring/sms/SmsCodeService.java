package com.cfido.commons.spring.sms;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.cfido.commons.beans.apiExceptions.SimpleApiException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.spring.dict.core.DictCoreService;
import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.utils.BaseCodeService;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;
import com.cfido.commons.utils.threadPool.IMyTask;
import com.cfido.commons.utils.utils.DateUtil;
import com.cfido.commons.utils.utils.LRULinkedHashMap;
import com.cfido.commons.utils.utils.LogUtil;
import com.cfido.commons.utils.utils.PhoneUtil;
import com.cfido.commons.utils.utils.TimeKeyUtil;

/**
 * <pre>
 * 短信验证码服务
 * </pre>
 * 
 * @author 梁韦江
 */
@Service
@ManagedResource(description = "短信验证码服务", objectName = "SmsCodeService:name=短信验证码-服务")
@ADomainOrder(domainName = CommonMBeanDomainNaming.DOMAIN_SMS, order = CommonMBeanDomainNaming.ORDER)
public class SmsCodeService extends BaseCodeService {

	/** 发送短信的任务 */
	class SendTask implements IMyTask {
		private final ISmsGateWay gateWay;
		private final String phone;
		private final String text;
		private final String remoteIp;

		public SendTask(ISmsGateWay gateWay, String phone, String text, String remoteIp) {
			super();
			this.gateWay = gateWay;
			this.phone = phone;
			this.text = text;
			this.remoteIp = remoteIp;
		}

		@Override
		public String getUniqueId() {
			return null;
		}

		@Override
		public void run() {
			SmsCodeService.this.doSendSms(this);
		}
	}

	public final static String VAR_CODE = "code";

	public final static String VAR_EXPIRE_MIIN = "expireInMin";

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SmsCodeService.class);

	/**
	 * 记录IP每日发送量的MAP的记录限制
	 */
	private static final int IP_MAP_SIZE = 1000;

	/** 发送失败的次数 */
	private final AtomicLong failCount = new AtomicLong();

	/** 最后出错的时间 */
	private Date lastErrorTime;

	/** 最后一次错误 */
	private SmsGateWayException lastException;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private SmsCodeProperties prop;

	@Autowired
	private SendSmsThreadPool threadPool;

	@Autowired
	private DictCoreService dictService;

	/** 是所有找到的网关 */
	private final LinkedList<ISmsGateWay> gateWayList = new LinkedList<>();

	/** 今天已经发送短信并且成功的次数 */
	private final AtomicLong dayCount = new AtomicLong();

	private int curTimeKey;

	/** 存储发送次数超过限制的IP */
	private final Set<String> outOfLimitIpSet = new HashSet<>();

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private final Map<String, AtomicLong> ipMap = new LRULinkedHashMap<>(IP_MAP_SIZE);

	/** 发送成功的次数 */
	private final AtomicLong successCount = new AtomicLong();

	/** 设置模板内容 */
	public void addTemplate(String type, String templateBody) {
		Assert.hasText(type, "类型不能为空");

		Assert.hasText(templateBody, "模板不能为空");

		// 模板的id
		String templateId = this.buildTemplateId(type);

		this.dictService.getRawText(templateId, templateBody);
	}

	/**
	 * 检查每日发送量是否已经超过限制了
	 */
	public void checkDayCount() throws SendSmsOutOfLimitException {
		// 需要先检查时间
		this.resetCounter();

		int dayLimit = this.prop.getPreDay();

		if (this.dayCount.get() >= dayLimit) {
			// 如果发送次数过多就抛错
			log.error("今天短信发送数量超过了上限 {}", dayLimit);
			throw new SendSmsOutOfLimitException(dayLimit);
		}
	}

	@Override
	public int getCodeExpireTimeInMin() {
		return this.prop.getExpireInMin();
	}

	/** 今天已经发送短信并且成功的次数 */
	@ManagedAttribute(description = "今天已经调用短信网关的成功次数")
	public long getDayCount() {
		return dayCount.get();
	}

	@ManagedAttribute(description = "发送失败的次数")
	public long getFailCount() {
		return failCount.get();
	}

	@Override
	public int getIntervalInSec() {
		return prop.getInterval();
	}

	@ManagedAttribute(description = "最后一次错误的信息")
	public String getLastErrorMsg() {
		if (this.lastException != null) {
			return this.lastException.getMessage();
		} else {
			return "无错误";
		}
	}

	@ManagedAttribute(description = "最后一次错误发送的时间")
	public String getLastErrorTime() {
		if (this.lastErrorTime != null) {
			return DateUtil.dateFormat(this.lastErrorTime);
		} else {
			return "无错误";
		}
	}

	@ManagedAttribute(description = "发送次数已经超过了上限的IP")
	public String getOutOfLimtIP() {
		StringBuffer sb = new StringBuffer();

		this.lock.readLock().lock();

		try {
			for (String ip : outOfLimitIpSet) {
				sb.append(ip);
				sb.append(",");
			}
		} finally {
			this.lock.readLock().unlock();
		}

		return sb.toString();
	}

	@ManagedAttribute(description = "发送成功的次数")
	public long getSuccessCount() {
		return successCount.get();
	}

	/** 发送短信验证码 */
	public void sendCode(String phone, String type, String remoteIp) throws BaseApiException {

		this.checkDayCount();
		this.checkIp(remoteIp);

		String key = this.buildPhoneKey(phone, type);

		// 检查是否发送过于频繁
		this.checkTooBusy(key);

		// 生成验证码
		String code = this.getRandomCode();

		// 保存验证码到redis
		this.saveCode(key, code);

		// 生成短信正文
		String text = this.buildSmsCodeText(type, code);

		// 发送短信
		if (isSend()) {
			this.sendSms(phone, text, remoteIp);
		}
	}

	/** 发送系统短信 */
	public void sendSystemSms(String phone, String text) throws BaseApiException {
		// 发送系统邮件没有限制
		this.sendSms(phone, text, null);
	}

	/** 验证短信验证码 */
	public void verifyCode(String phone, String type, String code) throws BaseApiException {
		this.verifyCode(phone, type, code, true);
	}

	/** 验证短信验证码 */
	public void verifyCode(String phone, String type, String code, boolean deleteKey) throws BaseApiException {
		String key = this.buildPhoneKey(phone, type);
		this.doVerifyCode(key, code, deleteKey);
	}

	public void deleteCode(String phone, String type) throws SimpleApiException {
		String key = this.buildPhoneKey(phone, type);
		this.deleteCodeKey(key);
	}

	/** 根据类型和电话号码，生成key */
	private String buildPhoneKey(String phone, String type) throws SimpleApiException {
		if (!PhoneUtil.isValidPhone(phone)) {
			throw new SimpleApiException("电话号码错误");
		}

		Assert.hasText(type, "类型不能为空");

		return type + ":" + phone;
	}

	/** 根据 类型，生成的短信模板 */
	private String buildSmsCodeText(String type, String code) {
		// 模板的id
		String templateId = this.buildTemplateId(type);

		// 创建用于生成正文的数据model
		Map<String, Object> dataModel = new HashMap<>();
		dataModel.put(VAR_CODE, code);
		dataModel.put(VAR_EXPIRE_MIIN, this.getCodeExpireTimeInMin());

		// 通过模板生成正文
		String text = this.dictService.processTemplate(templateId, dataModel);

		return text;
	}

	/** 根据短信类型，生成模板的名字 */
	private String buildTemplateId(String type) {
		Assert.hasText(type, "类型不能为空");

		return "sms.template." + type;
	}

	/** 检查ip限制 */
	private void checkIp(String ip) throws SendSmsOutOfLimitException {
		if (StringUtils.isEmpty(ip)) {
			return;
		}

		this.lock.writeLock().lock();
		try {
			AtomicLong count = this.ipMap.get(ip);
			int limit = this.prop.getPreIp();
			if (count != null) {
				if (count.get() >= limit) {
					log.error("IP:{} 今天发送短信数量超过了上限 {}", ip, limit);

					this.outOfLimitIpSet.add(ip);

					throw new SendSmsOutOfLimitException(limit);
				}
			}

		} finally {
			this.lock.writeLock().unlock();
		}

	}

	/** 由task调用，发送短信 */
	private void doSendSms(SendTask task) {
		try {
			task.gateWay.sendSms(task.phone, task.text, null);

			// 计数器 +1
			this.incSuccess(task.remoteIp);

		} catch (SmsGateWayException e) {
			// 失败次数+1
			this.failCount.incrementAndGet();

			// 记录故障发生的原因和时间
			this.lastException = e;
			this.lastErrorTime = new Date();

			LogUtil.traceError(log, e, "发送短信时，发生了错误");
		}
	}

	/**
	 * 成功次数+1
	 * 
	 * @param remoteIp
	 */
	private void incSuccess(String remoteIp) {
		this.successCount.incrementAndGet();

		// 每次增加次数时，需要先检查时间
		this.resetCounter();

		this.dayCount.incrementAndGet();

		// 这个IP的计数器+1
		this.lock.writeLock().lock();
		try {
			// 获得计数器
			AtomicLong count = this.ipMap.get(remoteIp);
			if (count == null) {
				// 如果不存在就添加
				count = new AtomicLong();
				this.ipMap.put(remoteIp, count);
			}

			count.incrementAndGet();
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	/**
	 * 判断是已经是第二天了，如果是就重置计数器
	 */
	private void resetCounter() {
		int key = TimeKeyUtil.getDayKey();
		if (key != this.curTimeKey) {
			// 如果不同了，就重置计数器
			this.dayCount.set(0);
			this.curTimeKey = key;
			this.ipMap.clear();
			this.outOfLimitIpSet.clear();
		}
	}

	private synchronized void sendSms(String phone, String text, String remoteIp) throws BaseApiException {
		if (!PhoneUtil.isValidPhone(phone)) {
			throw new SimpleApiException("电话号码错误");
		}

		if (StringUtils.isEmpty(text)) {
			throw new SimpleApiException("发送的内容不能为空");
		}

		ISmsGateWay gateWay = this.gateWayList.pollFirst();
		this.gateWayList.add(gateWay);

		this.threadPool.addNewTask(new SendTask(gateWay, phone, text, remoteIp));

	}

	@Override
	protected String getRedisKeyPrefix() {
		return "sms";
	}

	@PostConstruct
	protected void init() {

		Map<String, ISmsGateWay> map = this.applicationContext.getBeansOfType(ISmsGateWay.class);
		if (map.isEmpty()) {
			throw new RuntimeException("至少需要一个 实现了ISmsGateWay的服务");
		}

		this.gateWayList.addAll(map.values());

		log.info("初始化 短信验证码服务, 共找到 {} 个短信网关", this.gateWayList.size());
	}

	@Override
	public boolean isDebug() {
		return this.prop.isDebug();
	}

	@Override
	public boolean isSend() {
		return this.prop.isSend();
	}

}
