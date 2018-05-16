package com.cfido.commons.spring.weChat.controller;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.spring.utils.WebContextHolderHelper;
import com.cfido.commons.spring.weChat.KeyBeanInMaster;
import com.cfido.commons.spring.weChat.WeChatOAuthClient;
import com.cfido.commons.spring.weChat.WeChatOAuthClient.SCOPE;
import com.cfido.commons.spring.weChat.WeChatRedisKey;
import com.cfido.commons.utils.utils.StringUtilsEx;
import com.cfido.commons.utils.web.WebUtils;

/**
 * <pre>
 * 用于接受微信网页授权，并将code转发给其他应用。
 * 注意：这些被转发的其他应用的微信appId必须和本代理程序的appId一样
 * </pre>
 */
@Controller
@RequestMapping(WeChatUrls.PREFIX)
public class ProxyMasterController extends BaseController {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProxyMasterController.class);

	/** 用于保存发起方 */
	@Autowired
	private RedisTemplate<String, KeyBeanInMaster> agentMap;

	@Autowired
	private HttpServletRequest request;

	/**
	 * 获取验证码
	 * 
	 * @param scope：base或者userinfo
	 *            获取用户的方式
	 * @param key
	 *            传回给agent的内容
	 * @return
	 */
	@RequestMapping(WeChatUrls.MASTER_CALL)
	public String call(String scope, String key, String appId) {

		if (!this.wechatProperties.getAppId().equals(appId)) {
			// 如果appId不匹配，就直接返回
			log.warn("master收到邀请微信授权的请求，但appId非法, appId={}", appId);
			return this.getErrorPage();
		}

		// 解析出要传给微信的scope参数
		SCOPE scopeEnum = SCOPE.valueOf(scope);
		if (scopeEnum == null) {
			scopeEnum = SCOPE.snsapi_base;
		}

		// 将调用方的key和url保存下来，并获得唯一值state，用于回调
		String state = this.saveAgent(key);
		log.debug("master向微信发起授权请求前，保存发起方:key={}, scope={}", key, scopeEnum);

		// 发送认证请求给微信，将这个唯一值传给微信
		WeChatOAuthClient wechatOAuthClient = this.wechatProperties.newClient();
		String redirectUrl = WebContextHolderHelper.getFullPath(WeChatUrls.PREFIX + WeChatUrls.MASTER_CALLBACK);
		String authUrl = wechatOAuthClient.getAuthorizeUrl(redirectUrl, scopeEnum, state, true);
		return this.getRedirectUrl(authUrl);
	}

	/**
	 * 微信登录回调
	 */
	@RequestMapping(WeChatUrls.MASTER_CALLBACK)
	public String wechatCallback(String code, String state, Model model)
			throws IOException, BaseApiException {

		if (StringUtils.hasText(state) && StringUtils.hasText(code)) {
			// 如果收到授权码和state

			String redisKey = this.getRedisKey(state);
			KeyBeanInMaster bean = this.agentMap.opsForValue().get(redisKey);
			if (bean != null) {
				// 如果找到了原来保存下来的回调url，就回调
				String url = bean.toCallBackUrl(code);

				log.debug("获得微信授权后，从定向回agent端: url={} ", url);

				return this.getRedirectUrl(url);
			} else {
				log.warn("收到了微信回调，但找不回发起方，无法回调发起方, redisKey={}", redisKey);
			}
		} else {
			log.warn("收到了微信回调，但没有授权码，或者state值，code={}, state={}", code, state);
		}
		return this.getErrorPage();
	}

	private String getRedisKey(String id) {
		Assert.notNull(id, "id is null");
		return WeChatRedisKey.KEY_MASTER_PREFIX + id;
	}

	/** 将调用方的key和url保存下来，用于回调 */
	private String saveAgent(String key) {
		KeyBeanInMaster bean = new KeyBeanInMaster();
		bean.setAgentHost(WebUtils.getSchemeAndServerName(request));
		bean.setKey(key);
		String id = StringUtilsEx.randomUUID();
		String redisKey = this.getRedisKey(id);
		this.agentMap.opsForValue().set(redisKey, bean, 1, TimeUnit.HOURS);

		return id;
	}

	protected String getRedirectUrl(String url) {
		return "redirect:" + url;
	}
}
