package com.cfido.commons.spring.weChat;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.cfido.commons.beans.oauth.WeChatUserInfoBean;
import com.cfido.commons.spring.utils.WebContextHolderHelper;
import com.cfido.commons.spring.weChat.WeChatOAuthClient.SCOPE;
import com.cfido.commons.spring.weChat.controller.WeChatUrls;
import com.cfido.commons.utils.utils.StringUtilsEx;

@Service
public class ProxyAgentService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProxyAgentService.class);

	private final String SESSION_NAME_BEAN = "ProxyAgentService.bean";
	private final String SESSION_NAME_CODE = "ProxyAgentService.code";

	@Autowired
	private WeChatProperties wechatProperties;

	@Autowired
	private RedisTemplate<String, KeyBeanInAgent> urlMap;

	/** 获取用户信息，如果没有，表示尚未通过授权获得 */
	public WeChatUserInfoBean getUserInfo() {
		HttpServletRequest request = WebContextHolderHelper.getRequest();
		if (request != null) {
			return (WeChatUserInfoBean) request.getSession().getAttribute(SESSION_NAME_BEAN);
		}
		return null;
	}

	/** 清楚保存在session中的用户信息 */
	public void clearUserInfo() {
		HttpServletRequest request = WebContextHolderHelper.getRequest();
		if (request != null) {
			request.getSession().removeAttribute(SESSION_NAME_BEAN);
		}
	}

	/** 获取授权码 */
	public String getCode() {
		HttpServletRequest request = WebContextHolderHelper.getRequest();
		if (request != null) {
			return (String) request.getSession().getAttribute(SESSION_NAME_CODE);
		}
		return null;
	}

	/** 将授权码保存到session中 */
	public void saveCode(String code) {
		HttpServletRequest request = WebContextHolderHelper.getRequest();
		if (request != null) {
			request.getSession().setAttribute(SESSION_NAME_CODE, code);
		}
	}

	/** 将用户信息保存到session中 */
	public void saveUserInfoBean(WeChatUserInfoBean bean) {
		HttpServletRequest request = WebContextHolderHelper.getRequest();
		if (request != null) {
			log.debug("将获得到的微信用户信息保存到session \n{}", JSON.toJSONString(bean, true));
			request.getSession().setAttribute(SESSION_NAME_BEAN, bean);
		}
	}

	public KeyBeanInAgent getUrlBeanByKey(String key) {
		if (StringUtils.hasText(key)) {
			String redisKey = this.getRedisKey(key);
			return this.urlMap.opsForValue().get(redisKey);
		}
		return null;
	}

	private String getRedisKey(String id) {
		Assert.notNull(id, "id is null");
		return WeChatRedisKey.KEY_AGENT_PREFIX + id;
	}

	/** 将当前的url保存下来，用于回调 */
	private String saveUrl(HttpServletRequest request, SCOPE scope) {
		KeyBeanInAgent bean = KeyBeanInAgent.create(request, scope);
		String key = StringUtilsEx.randomUUID();
		String redisKey = this.getRedisKey(key);
		this.urlMap.opsForValue().set(redisKey, bean, 1, TimeUnit.HOURS);

		log.debug("准备向master发起认证请求，当前url:{}", bean.getCurUrl());

		return key;
	}

	/** 获取向master请求微信授权的url */
	public String redirectToMaster(SCOPE scope) {
		HttpServletRequest request = WebContextHolderHelper.getRequest();

		if (request != null) {

			String key = this.saveUrl(request, scope);

			StringBuilder sb = new StringBuilder();

			sb.append("redirect:");

			if (StringUtils.hasLength(this.wechatProperties.getMasterUrl())) {
				// 看看是否有master是否在其他机器
				sb.append(this.wechatProperties.getMasterUrl());
			}

			// 例子 /wechatProxy/masterCall
			sb.append(WeChatUrls.PREFIX).append(WeChatUrls.MASTER_CALL);

			// 参数 (String scope, String key, String appId) {
			sb.append("?");
			sb.append("appId=").append(this.wechatProperties.getAppId());
			sb.append("&");
			sb.append("scope=").append(scope.toString());
			sb.append("&");
			sb.append("key=").append(key);

			String url = sb.toString();
			log.debug("重定向到master，向微信发起授权请求。url={}", url);

			return url;
		}
		return null;
	}

}
