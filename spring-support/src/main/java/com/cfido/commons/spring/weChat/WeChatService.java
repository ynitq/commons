package com.cfido.commons.spring.weChat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.cfido.commons.beans.oauth.AccessTokenBean;
import com.cfido.commons.beans.oauth.WechatJsSDKBean;
import com.cfido.commons.beans.oauth.WechatTicketBean;
import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.jmxInWeb.core.JmxInWebService;
import com.cfido.commons.spring.monitor.MonitorClientService;
import com.cfido.commons.spring.monitor.MonitorMsgTypeEnum;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;
import com.cfido.commons.spring.utils.WebContextHolderHelper;
import com.cfido.commons.utils.utils.DateUtil;
import com.cfido.commons.utils.utils.EncryptUtil;
import com.cfido.commons.utils.utils.HttpUtil;
import com.cfido.commons.utils.utils.LogUtil;

/**
 * <pre>
 * 微信服务，包括获取ticket，jssdk配置等
 * 
 * 文档参加：https://mp.weixin.qq.com/wiki
 * 
 * </pre>
 * 
 * @author 梁韦江 2017年6月13日
 */
@Service
public class WeChatService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WeChatService.class);

	@Autowired
	private WeChatProperties prop;

	@Autowired
	private RedisTemplate<String, AccessTokenBean> accessTokenCache;

	@Autowired
	private RedisTemplate<String, WechatTicketBean> jsapiTicketCache;

	@Autowired(required = false)
	private MonitorClientService monitorClient;

	@Autowired
	private JmxInWebService jmxInWebService;

	@ManagedResource(description = "微信服务")
	@ADomainOrder(order = CommonMBeanDomainNaming.ORDER, domainName = CommonMBeanDomainNaming.DOMAIN)
	public class WeChatServiceMBean {

		@ManagedAttribute(description = "微信appId")
		public String getAppId() {
			return WeChatService.this.prop.getAppId();
		}

		@ManagedAttribute(description = "微信jsapi ticket")
		public Map<String, Object> getJsApiTicket() throws WeChatAccessFailException {

			WechatTicketBean bean = WeChatService.this.getJsApiTicket();

			Map<String, Object> map = new HashMap<>();
			map.put("ticket", bean);
			map.put("获取时间", DateUtil.dateFormat(new Date(bean.getCreateTime())));
			return map;
		}

		@ManagedAttribute(description = "微信access token")
		public Map<String, Object> getAccessToken() throws WeChatAccessFailException {

			AccessTokenBean bean = WeChatService.this.getAccessToken();

			Map<String, Object> map = new HashMap<>();
			map.put("token", bean);
			map.put("获取时间", DateUtil.dateFormat(new Date(bean.getCreateTime())));
			return map;
		}

	}

	@PostConstruct
	protected void init() {
		this.jmxInWebService.register(new WeChatServiceMBean());
	}

	/**
	 * 获取access token
	 * 
	 * @throws WeChatAccessFailException
	 */
	public synchronized AccessTokenBean getAccessToken() throws WeChatAccessFailException {
		AccessTokenBean token = this.accessTokenCache.opsForValue().get(WeChatRedisKey.KEY_ACCESS_TOKEN);
		if (token == null || token.checkIsExpired()) {
			if (token == null) {
				log.debug("微信access token 不存在，需要获取");
			} else {
				log.debug("微信access token 已经超时，需要重新获取");
			}

			String url = "https://api.weixin.qq.com/cgi-bin/token";
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("grant_type", "client_credential");
			paramMap.put("appid", this.prop.getAppId());
			paramMap.put("secret", this.prop.getAppSecret());

			try {
				token = HttpUtil.requestJson(AccessTokenBean.class, url, paramMap, false, null);
				this.accessTokenCache.opsForValue().set(WeChatRedisKey.KEY_ACCESS_TOKEN, token);

				if (log.isDebugEnabled()) {
					log.debug("成功获取 access token:\n\t{}", JSON.toJSONString(token, true));
				}

			} catch (IOException e) {
				String msg = LogUtil.getTraceString("向微信请求access_token时，失败,", e);
				this.monitorClient.reportMsgToServer(MonitorMsgTypeEnum.ERROR, msg);
				throw new WeChatAccessFailException(e);
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("从redis中获取 access token:\n\t{}", JSON.toJSONString(token, true));
			}
		}
		return token;
	}

	/**
	 * 获取 jsapi 的 ticket
	 * 
	 * @throws WeChatAccessFailException
	 */
	public synchronized WechatTicketBean getJsApiTicket() throws WeChatAccessFailException {

		WechatTicketBean ticket = this.jsapiTicketCache.opsForValue().get(WeChatRedisKey.KEY_JSAPI);
		if (ticket == null || ticket.checkIsExpired()) {
			if (ticket == null) {
				log.debug("微信 jsapi ticket 不存在，需要获取");
			} else {
				log.debug("微信 jsapi ticket 已经超时，需要重新获取");
			}

			final String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("type", "jsapi");
			paramMap.put("access_token", this.getAccessToken().getAccess_token());

			try {
				ticket = HttpUtil.requestJson(WechatTicketBean.class, url, paramMap, false, null);
				this.jsapiTicketCache.opsForValue().set(WeChatRedisKey.KEY_JSAPI, ticket);

				if (log.isDebugEnabled()) {
					log.debug("成功获取 jsapi ticket:\n\t{}", JSON.toJSONString(ticket, true));
				}

			} catch (IOException e) {
				String msg = LogUtil.getTraceString("向微信请求jsapi ticket时，失败,", e);
				if (this.monitorClient != null) {
					this.monitorClient.reportMsgToServer(MonitorMsgTypeEnum.ERROR, msg);
				}
				throw new WeChatAccessFailException(e);
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("从redis中获取 jsapi ticket:\n\t{}", JSON.toJSONString(ticket, true));
			}
		}

		return ticket;
	}

	/**
	 * 获取实现jsSdk时所需要的参数，但不包含jsApiList。注意：官网文档写的只支持 80 和 https的443
	 * 
	 * @throws WeChatAccessFailException
	 */
	public WechatJsSDKBean getJsSdkConfig(String url) throws WeChatAccessFailException {
		if (StringUtils.isEmpty(url)) {
			return null;
		}

		WechatJsSDKBean res = new WechatJsSDKBean(url, this.prop.getAppId());
		// 初始化时已经生成了 nonceStr和 timestamp， 需要生成sign

		// 注意这里参数名必须全部小写，且必须有序
		StringBuilder sb = new StringBuilder();
		sb.append("jsapi_ticket=").append(this.getJsApiTicket().getTicket())
				.append("&noncestr=").append(res.getNonceStr())
				.append("&timestamp=").append(res.getTimestamp())
				.append("&url=").append(url);

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(sb.toString().getBytes("UTF-8"));
			res.setSignature(EncryptUtil.byteToHex(crypt.digest()));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			LogUtil.traceError(log, e, "出现了SHA-1 或者 UTF-8 不存在的古怪错误");
		}
		return res;
	}

	/**
	 * 获取实现jsSdk时所需要的参数，默认使用当前请求的来源url
	 * 
	 * @throws WeChatAccessFailException
	 */
	public WechatJsSDKBean getJsSdkConfigFromReferer() throws WeChatAccessFailException {
		String url = WebContextHolderHelper.getRequest().getHeader("referer");
		return this.getJsSdkConfig(url);
	}
}
