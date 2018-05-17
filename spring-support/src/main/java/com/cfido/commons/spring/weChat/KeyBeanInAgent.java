package com.cfido.commons.spring.weChat;

import javax.servlet.http.HttpServletRequest;

import com.cfido.commons.enums.WeChatAuthScope;
import com.cfido.commons.utils.web.WebUtils;

/**
 * <pre>
 * 用于在agent方，发起请求前，先保存当前的url
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public class KeyBeanInAgent {

	private String curUrl;
	private WeChatAuthScope scope;

	public String getCurUrl() {
		return curUrl;
	}

	public void setCurUrl(String curUrl) {
		this.curUrl = curUrl;
	}

	public WeChatAuthScope getScope() {
		return scope;
	}

	public void setScope(WeChatAuthScope scope) {
		this.scope = scope;
	}

	public static KeyBeanInAgent create(HttpServletRequest request, WeChatAuthScope scope) {
		KeyBeanInAgent bean = new KeyBeanInAgent();
		bean.curUrl = WebUtils.getRequestURL(request, true);
		bean.scope = scope;
		return bean;
	}

}
