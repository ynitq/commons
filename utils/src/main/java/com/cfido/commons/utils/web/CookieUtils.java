package com.cfido.commons.utils.web;

import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import com.cfido.commons.utils.utils.EncryptUtil;

/**
 * <pre>
 * 常见的cookie操作
 * </pre>
 * 
 * @author 梁韦江 2015年8月7日
 */
public class CookieUtils {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CookieUtils.class);

	private static final String DES_KEY = "梁韦江是个大好人";

	/**
	 * 用url编码的方式存储cookie
	 * 
	 * @param response
	 * @param cookieName
	 * @param cookieValue
	 * @param cookieMaxSecondAge
	 */
	public static void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue,
			long cookieMaxSecondAge) {
		addCookie(response, request.getContextPath(), cookieName, cookieValue, "", cookieMaxSecondAge, false);
	}

	/**
	 * 用url编码的方式存储cookie
	 * 
	 * @param response
	 * @param cookiePath
	 * @param cookieName
	 * @param cookieValue
	 * @param cookieDomain
	 * @param cookieMaxSecondAge
	 */
	public static void addCookie(HttpServletResponse response, String cookiePath, String cookieName, String cookieValue,
			String cookieDomain, long cookieMaxSecondAge) {
		addCookie(response, cookiePath, cookieName, cookieValue, cookieDomain, cookieMaxSecondAge, false);
	}

	private static void addCookie(HttpServletResponse response, String cookiePath, String cookieName, String cookieValue,
			String cookieDomain, long cookieMaxSecondAge, boolean isEncrypt) {
		String saveString = "";
		try {
			if (isEncrypt) {
				saveString = EncryptUtil.desEncryptAscllString(DES_KEY, cookieValue);
			} else {
				saveString = URLEncoder.encode(cookieValue, "UTF-8");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		Cookie cookie = new Cookie(cookieName, saveString);
		if (StringUtils.hasText(cookieDomain)) {
			cookie.setDomain(cookieDomain);
		}
		cookie.setPath(cookiePath);
		cookie.setMaxAge((int) cookieMaxSecondAge);
		response.addCookie(cookie);
	}

	/**
	 * 将值加密后，放到cookie中
	 * 
	 * @param response
	 * @param cookieName
	 * @param cookieValue
	 * @param cookieMaxSecondAge
	 */
	public static void addEncryptCookie(HttpServletResponse response, String cookieName, String cookieValue,
			long cookieMaxSecondAge) {
		addCookie(response, "", cookieName, cookieValue, "", cookieMaxSecondAge, true);
	}

	/**
	 * 将值加密后，放入cookie中
	 * 
	 * @param response
	 * @param cookiePath
	 * @param cookieName
	 * @param cookieValue
	 * @param cookieDomain
	 * @param cookieMaxSecondAge
	 * @throws Exception
	 */
	public static void addEncryptCookie(HttpServletResponse response, String cookiePath, String cookieName, String cookieValue,
			String cookieDomain, long cookieMaxSecondAge) {
		addCookie(response, cookiePath, cookieName, cookieValue, cookieDomain, cookieMaxSecondAge, true);
	}

	/**
	 * 从数组中，根据名字寻找cookie
	 * 
	 * @param cookies
	 * @param cookieName
	 * @return
	 */
	private static Cookie findCookieFromArray(Cookie cookies[], String cookieName) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookieName.equals(cookie.getName())) {
					// 找到就返回
					return cookie;
				}
			}
		}
		return null;
	}

	/**
	 * 从request中清除，用于防止同一线程重复读取
	 * 
	 * @param request
	 * @param cookieName
	 */
	public static void removeCookie(HttpServletRequest request, String cookieName) {
		Cookie cookies[] = request.getCookies();
		if (cookies == null) {
			return;
		}

		Cookie cookie = findCookieFromArray(cookies, cookieName);
		if (cookie != null) {
			cookie.setValue(null);
			cookie.setMaxAge(0);
		}
	}

	/**
	 * 获得cookie的值
	 * 
	 * @param request
	 * @param cookieName
	 *            名字
	 * @param isEncrypt
	 *            是否需要解密
	 * @return
	 */
	public static String getCookieValue(HttpServletRequest request, String cookieName, boolean isEncrypt) {

		Cookie cookies[] = request.getCookies();
		if (cookies == null) {
			// 根本就没有cookie
			return null;
		}

		Cookie cookie = findCookieFromArray(cookies, cookieName);
		if (cookie == null) {
			// 没有这个名字的cookie
			return null;
		}

		String value = cookie.getValue();
		if (value == null) {
			// 有这个名字的cookie，但没有值
			return null;
		}

		try {
			if (isEncrypt) {
				return EncryptUtil.desDecryptAscll(DES_KEY, cookie.getValue());
			} else {
				return URLDecoder.decode(cookie.getValue(), "UTF-8");
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 删除cookie，实际是通过写一个同名的，但立刻过期的cookie
	 * 
	 * @param response
	 * @param cookiePath
	 * @param cookieName
	 * @param cookieDomain
	 */
	public static void removeCookie(HttpServletResponse response, String cookiePath, String cookieName, String cookieDomain) {
		Cookie cookie = new Cookie(cookieName, null);
		if (StringUtils.hasText(cookieDomain)) {
			cookie.setDomain(cookieDomain);
		}
		cookie.setPath(cookiePath);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
}
