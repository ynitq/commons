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

/**
 * <pre>
 * LoginCheck的额外服务，用于监听AWechatRequestAuth注解
 * 
 * 如果被调用的方法有这个注解，就需要检查能获取微信用户信息
 * 如果没有微信用户信息，就会将调用proxyAgentService的相关功能
 * 将请求转发给 wechat.masterUrl 中定义的服务器，让那台在外网的服务器向微信要求权限
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
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
