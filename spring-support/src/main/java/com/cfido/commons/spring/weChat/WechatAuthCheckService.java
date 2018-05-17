package com.cfido.commons.spring.weChat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cfido.commons.annotation.other.AWechatRequestAuth;
import com.cfido.commons.beans.oauth.WeChatUserInfoBean;
import com.cfido.commons.spring.security.ActionInfo;
import com.cfido.commons.spring.security.ILoginCheckExtService;
import com.cfido.commons.spring.security.LoginExtCheckException;

@Service
public class WechatAuthCheckService implements ILoginCheckExtService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WechatAuthCheckService.class);

	@Autowired
	private ProxyAgentService proxyAgentService;

	public void afterCheckRight(ActionInfo info) throws LoginExtCheckException {
	}

	@Override
	public void beforeCheckRight(ActionInfo info, HttpServletResponse response, HttpServletRequest request)
			throws LoginExtCheckException {
	}

	@Override
	public void afterCheckRight(ActionInfo info, HttpServletResponse response, HttpServletRequest request)
			throws LoginExtCheckException {

		AWechatRequestAuth anno = info.getAnnotationFromMethod(AWechatRequestAuth.class);
		if (anno == null) {
			// 如果不需要微信登录，就直接返回
			return;
		}

		WeChatUserInfoBean userInfo = this.proxyAgentService.getUserInfo();

		if (userInfo == null) {
			log.debug("没有找到微信用户信息，需要重定向到master，获取用户信息");

			String redirectUrl = this.proxyAgentService.getRedirectToMasterUrl(anno.scope());
			throw LoginExtCheckException.redirect(redirectUrl);
		}
	}

}
