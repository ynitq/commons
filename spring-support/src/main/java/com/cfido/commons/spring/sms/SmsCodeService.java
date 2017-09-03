package com.cfido.commons.spring.sms;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.cfido.commons.beans.apiExceptions.SimpleApiException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.spring.dict.core.DictCoreService;
import com.cfido.commons.spring.utils.BaseCodeService;
import com.cfido.commons.utils.threadPool.IMyTask;

/**
 * <pre>
 * 短信验证码服务
 * </pre>
 * 
 * @author 梁韦江
 */
@Service
public class SmsCodeService extends BaseCodeService {

	public final static String VAR_CODE = "code";
	public final static String VAR_EXPIRE_MIIN = "expireInMin";

	/** 发送短信的任务 */
	class SendTask implements IMyTask {
		private final ISmsGateWay gateWay;
		private final String phone;
		private final String text;

		public SendTask(ISmsGateWay gateWay, String phone, String text) {
			super();
			this.gateWay = gateWay;
			this.phone = phone;
			this.text = text;
		}

		@Override
		public String getUniqueId() {
			return null;
		}

		@Override
		public void run() {
			try {
				this.gateWay.sendSms(phone, text, null);
			} catch (SmsGateWayException e) {
				log.error("发送短信 是发送了错误", e);
			}
		}

	}

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SmsCodeService.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private SmsCodeProperties prop;

	@Autowired
	private SendSmsThreadPool threadPool;

	@Autowired
	private DictCoreService dictService;

	private final LinkedList<ISmsGateWay> gateWayList = new LinkedList<>();

	@Override
	public int getCodeExpireTimeInMin() {
		return this.prop.getExpireInMin();
	}

	@Override
	public int getIntervalInSec() {
		return prop.getInterval();
	}

	/** 发送短信验证码 */
	public void sendCode(String phone, String type, String remoteIp) throws BaseApiException {
		String key = this.buildPhoneKey(phone, type);

		// 生成验证码
		String code = this.getRandomCode();

		// 保存验证码到redis
		this.saveCode(key, code);

		// 生成短信正文
		String text = this.buildSmsCodeText(type, code);

		// 发送短信
		this.sendSms(phone, text);
	}

	/** 发送系统短信 */
	public void sendSystemSms(String phone, String text) throws BaseApiException {
		this.sendSms(phone, text);
	}

	/** 验证短信验证码 */
	public void verifyCode(String phone, String type, String code) throws BaseApiException {
		String key = this.buildPhoneKey(phone, type);
		this.doVerifyCode(key, code, true);
	}

	/** 根据类型和电话号码，生成key */
	private String buildPhoneKey(String phone, String type) throws SimpleApiException {
		if (!PhoneUtil.isValidPhone(phone)) {
			throw new SimpleApiException("电话号码错误");
		}

		Assert.hasText(type, "类型不能为空");

		return type + ":" + phone;
	}

	/** 设置模板内容 */
	public void addTemplate(String type, String templateBody) {
		Assert.hasText(type, "类型不能为空");

		Assert.hasText(templateBody, "模板不能为空");

		// 模板的id
		String templateId = this.buildTemplateId(type);

		this.dictService.getRawText(templateId, templateBody);
	}

	/** 根据短信类型，生成模板的名字 */
	private String buildTemplateId(String type) {
		Assert.hasText(type, "类型不能为空");

		return "sms.template." + type;
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

	private synchronized void sendSms(String phone, String text) throws BaseApiException {
		if (!PhoneUtil.isValidPhone(phone)) {
			throw new SimpleApiException("电话号码错误");
		}

		if (StringUtils.isEmpty(text)) {
			throw new SimpleApiException("发送的内容不能为空");
		}

		ISmsGateWay gateWay = this.gateWayList.pollFirst();
		this.gateWayList.add(gateWay);

		this.threadPool.addNewTask(new SendTask(gateWay, phone, text));
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

}
