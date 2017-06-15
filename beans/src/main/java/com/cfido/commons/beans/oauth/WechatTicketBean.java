package com.cfido.commons.beans.oauth;

/**
 * <pre>
 * 获取微信 ticket时返回的内容，这个ticket可用于微信的js_sdk
 * </pre>
 * 
 * @author 梁韦江 2017年6月13日
 */
public class WechatTicketBean extends ExpiresInBean {

	private String ticket; // kgt8ON7yVITDhtdwci0qeWMnVLCY76ge2vV_EDbqRg0ON39mXl0RhnP-B58g7afpv-5_pmwu_4_v7Dj4rqrYjQ",

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
}
