package com.cfido.commons.spring.weChat.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.cfido.commons.spring.weChat.WeChatProperties;

class BaseController {
	@Autowired
	protected WeChatProperties wechatProperties;

	protected String getErrorPage() {
		return this.wechatProperties.getErrorPage();
	}
}
