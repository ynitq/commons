package com.cfido.commons.spring.weChat.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.exceptions.WeChatApiException;
import com.cfido.commons.beans.oauth.WeChatUserInfoBean;
import com.cfido.commons.spring.weChat.KeyBeanInAgent;
import com.cfido.commons.spring.weChat.KeyBeanInMaster;
import com.cfido.commons.spring.weChat.ProxyAgentService;
import com.cfido.commons.spring.weChat.WeChatOAuthClient;
import com.cfido.commons.spring.weChat.WeChatProperties;
import com.cfido.commons.utils.web.WebUtils;

/**
 * <pre>
 * 用于接受微信网页授权，并将code转发给其他应用。
 * 注意：这些被转发的其他应用的微信appId必须和本代理程序的appId一样
 * </pre>
 */
@Controller
@RequestMapping(WeChatUrls.PREFIX)
public class ProxyAgentController {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProxyAgentController.class);

	@Autowired
	private WeChatProperties wechatProperties;

	@Autowired
	private ProxyAgentService proxyAgentService;

	/**
	 * 根据授权码向微信请求用户信息
	 * 
	 * @param code
	 *            授权码
	 */
	private WeChatUserInfoBean getUserInfoFromWeixin(String code) throws IOException, WeChatApiException {
		WeChatOAuthClient client = this.wechatProperties.newClient();
		client.onAuthorizeCallback(code);
		WeChatUserInfoBean bean = client.getUserInfoBean();
		return bean;
	}

	/**
	 * master回调
	 * 
	 * @see KeyBeanInMaster#toCallBackUrl(String)
	 */
	@RequestMapping(WeChatUrls.AGENT_CALLBACK)
	public String wechatCallback(String code, String key)
			throws IOException, BaseApiException {

		if (StringUtils.hasText(key) && StringUtils.hasText(code)) {
			// 如果收到授权码和key
			KeyBeanInAgent bean = this.proxyAgentService.getUrlBeanByKey(key);
			if (bean != null) {
				WeChatUserInfoBean userInfo = this.getUserInfoFromWeixin(code);
				this.proxyAgentService.saveCode(code);
				this.proxyAgentService.saveUserInfoBean(userInfo);

				log.debug("收到master回调，重定向回 code={}, url={}", code, bean.getCurUrl());

				return WebUtils.getRedirect(bean.getCurUrl());
			} else {
				log.warn("收到master回调，但找不到key对应的url。code={}, key={}", code, key);
			}
		} else {
			log.warn("收到master回调，但参数缺失。code={}, key={}", code, key);
		}
		return null;
	}
}
