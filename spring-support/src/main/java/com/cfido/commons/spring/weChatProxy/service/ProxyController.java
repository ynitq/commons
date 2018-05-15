package com.cfido.commons.spring.weChatProxy.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.spring.utils.WebContextHolderHelper;
import com.cfido.commons.spring.weChat.WeChatProperties;
import com.cfido.commons.utils.oauth.WeChatOAuthClient;
import com.cfido.commons.utils.oauth.WeChatOAuthClient.SCOPE;

/**
 * <pre>
 * 用于接受微信网页授权，并将code转发给其他应用。
 * 注意：这些被转发的其他应用的微信appId必须和本代理程序的appId一样
 * </pre>
 */
@Controller
@RequestMapping("/wechatProxy")
public class ProxyController {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProxyController.class);

	@Autowired
	private WeChatProperties wechatProperties;

	@Autowired
	private CallBackUrlService callBackUrlService;

	@RequestMapping("/getCode")
	public String getCode(String state) {
		WeChatOAuthClient wechatOAuthClient = this.wechatProperties.newClient();
		String redirectUrl = WebContextHolderHelper.getFullPath("/wechatProxy/callback");
		String authUrl = wechatOAuthClient.getAuthorizeUrl(redirectUrl, SCOPE.snsapi_base, state, true);
		return this.getRedirectUrl(authUrl);
	}

	@RequestMapping("/getUserInfoCode")
	public String getUserInfoCode() {
		WeChatOAuthClient wechatOAuthClient = this.wechatProperties.newClient();
		String redirectUrl = WebContextHolderHelper.getFullPath("/wechatProxy/callback");
		String authUrl = wechatOAuthClient.getAuthorizeUrl(redirectUrl, SCOPE.snsapi_userinfo, null, true);
		return this.getRedirectUrl(authUrl);
	}

	protected String getRedirectUrl(String url) {
		return "redirect:" + url;
	}

	/**
	 * 微信登录回调
	 */
	@RequestMapping("/callback")
	public String wechatCallback(String code, String state, Model model)
			throws IOException, BaseApiException {

		String url = this.callBackUrlService.getUrl(state, code);
		if (StringUtils.hasText(url)) {
			log.debug("根据state={}, 重定向到 {}", state, url);
			return "redirect:" + url;
		} else {
			model.addAttribute("code", code);
			return "getCode";
		}
	}
}
