package com.cfido.commons.spring.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cfido.commons.loginCheck.IWebUser;

/**
 * <pre>
 * Debug模式 配置
 * 
 * 默认参数
 * loginCheck.rememberMe.cookieName = remember-me
 * loginCheck.rememberMe.paramName = remember-me 
 * loginCheck.rememberMe.cookieAge = 7*24*60*60 秒，就是7天了 
 * loginCheck.rememberMe.key = 梁韦江是个好人
 * loginCheck.rememberMe.allwaysRememmberMe = false
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月26日
 */
@ManagedResource(description = "LoginCheck 配置", objectName = "Common配置:name=LoginCheckProperties")
@ConfigurationProperties(prefix = "loginCheck")
public class LoginCheckProperties {

	private RememberMe rememberMe = new RememberMe();

	/**
	 * 登陆页的url,用于权限校验不通过的时候，重定向到登录页
	 */
	private String loginUrl = "/login";

	public static class RememberMe {

		/** cookie 名字 前缀 */
		private String cookieName = "remember_me";

		/** remember-me 的参数名 */
		private String paramName = "remember_me";

		/** cookie 有效期，单位：秒 */
		private int cookieAge = 7 * 24 * 60 * 60;

		/** 生成签名时，附加的key */
		private String key = "梁韦江是个好人";

		/** 是否总是 记录登录状态 */
		private boolean allwaysRememmberMe = false;

		public String getCookieName() {
			return cookieName;
		}

		/**
		 * 根据用户对象的类型生成cookie name
		 * 
		 * @param clazz
		 * @return
		 */
		public String getCookieName(Class<? extends IWebUser> clazz) {
			return cookieName + "_" + clazz.getSimpleName();
		}

		public void setCookieName(String cookieName) {
			this.cookieName = cookieName;
		}

		public String getParamName() {
			return paramName;
		}

		public void setParamName(String paramName) {
			this.paramName = paramName;
		}

		public int getCookieAge() {
			return cookieAge;
		}

		public void setCookieAge(int cookieAge) {
			this.cookieAge = cookieAge;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public boolean isAllwaysRememmberMe() {
			return allwaysRememmberMe;
		}

		public void setAllwaysRememmberMe(boolean allwaysRememmberMe) {
			this.allwaysRememmberMe = allwaysRememmberMe;
		}
	}

	public RememberMe getRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(RememberMe rememberMe) {
		this.rememberMe = rememberMe;
	}
	
	@ManagedAttribute(description = "Cookie名字")
	public String getRememberMeCookieName() {
		return this.rememberMe.getCookieName();
	}

	@ManagedAttribute
	public void setRememberMeCookieName(String cookieName) {
		this.rememberMe.setCookieName(cookieName);
	}

	@ManagedAttribute(description = "引发Remenber的参数名")
	public String getRememberMeParamName() {
		return this.rememberMe.getParamName();
	}

	@ManagedAttribute
	public void setRememberMeParamName(String paramName) {
		this.rememberMe.setParamName(paramName);
	}

	@ManagedAttribute(description = "cookie的有效期，秒")
	public int getRememberMeCookieAge() {
		return this.rememberMe.getCookieAge();
	}

	@ManagedAttribute
	public void setRememberMeCookieAge(int cookieAge) {
		this.rememberMe.setCookieAge(cookieAge);
	}

	@ManagedAttribute(description = "签名需要的key")
	public String getRememberMeKey() {
		return this.rememberMe.getKey();
	}

	@ManagedAttribute
	public void setRememberMeKey(String key) {
		this.rememberMe.setKey(key);
	}

	@ManagedAttribute(description = "是否总是记录登录状态")
	public boolean isAllwaysRememmberMe() {
		return this.rememberMe.isAllwaysRememmberMe();
	}

	@ManagedAttribute
	public void setAllwaysRememmberMe(boolean allwaysRememmberMe) {
		this.rememberMe.setAllwaysRememmberMe(allwaysRememmberMe);
	}

	@ManagedAttribute(description = "登录url")
	public String getLoginUrl() {
		return loginUrl;
	}

	@ManagedAttribute
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}
}
