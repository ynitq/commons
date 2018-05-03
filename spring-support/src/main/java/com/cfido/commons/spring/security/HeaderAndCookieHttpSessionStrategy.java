package com.cfido.commons.spring.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.session.Session;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.CookieSerializer.CookieValue;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.util.StringUtils;

/**
 * <pre>
 * ajax跨域调用api时，没有有session，所以我们需要能够从header中获取sessionId
 * 
 * 这个类就是为了配合 Spring Session项目，可以先在header中查询sessionId，如果没有，再去cookie中查
 * </pre>
 * 
 * @author 梁韦江 2017年5月9日
 * 
 * @see HeaderHttpSessionStrategy 参考这里，从header中获取
 * 
 * @see CookieHttpSessionStrategy 参考这里，从cookie中获取
 */
public class HeaderAndCookieHttpSessionStrategy implements HttpSessionStrategy {
	public static final String HEADER_NAME = "x-auth-token";

	public static final String ID_ATTR_NAME = HeaderAndCookieHttpSessionStrategy.class.getName() + ".sessionId";

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(HeaderAndCookieHttpSessionStrategy.class);

	private final CookieSerializer cookieSerializer = new DefaultCookieSerializer();

	@Override
	public String getRequestedSessionId(HttpServletRequest request) {
		// 先在Request的属性中找
		String sessionId = (String) request.getAttribute(ID_ATTR_NAME);

		if (StringUtils.isEmpty(sessionId)) {
			// 如果找不到，就从header中找
			sessionId = this.findSessionIdFromRequest(request);

			if (!StringUtils.isEmpty(sessionId)) {
				// 如果在header或者cookie中找到了，就保存到request中
				log.debug("找到 sessionId ，保存到request.attribute中");
				request.setAttribute(ID_ATTR_NAME, sessionId);
			}
		}

		return sessionId;
	}

	private String findSessionIdFromRequest(HttpServletRequest request) {
		// 尝试header
		String sessionId = request.getHeader(HEADER_NAME);
		if (StringUtils.hasText(sessionId)) {
			log.debug("在Headere中找到 SessionId:{}", sessionId);
			return sessionId;
		}

		// 尝试参数
		sessionId = request.getParameter(HEADER_NAME);
		if (StringUtils.hasText(sessionId)) {
			log.debug("在参数中找到 SessionId:{}", sessionId);
			return sessionId;
		}

		// 尝试cookie
		sessionId = this.getSessionIdFromCookie(request);
		if (StringUtils.hasText(sessionId)) {
			log.debug("在Cookie中找到 SessionId:{}", sessionId);
			return sessionId;
		}

		return null;
	}

	@Override
	public void onNewSession(Session session, HttpServletRequest request, HttpServletResponse response) {

		String sessionId = session.getId();

		log.debug("onNewSession时，保存SessionId:{} 到 header和 cookie", sessionId);

		response.setHeader("Access-Control-Expose-Headers", HEADER_NAME);
		response.setHeader(HEADER_NAME, sessionId);

		this.cookieSerializer.writeCookieValue(new CookieValue(request, response, sessionId));
	}

	@Override
	public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {

		log.debug("onInvalidateSession时，删除SessionId");

		response.setHeader(HEADER_NAME, "");
		this.cookieSerializer.writeCookieValue(new CookieValue(request, response, ""));
	}

	private String getSessionIdFromCookie(HttpServletRequest request) {

		List<String> cookieValues = this.cookieSerializer.readCookieValues(request);
		return cookieValues.isEmpty() ? "" : cookieValues.get(0);
	}

}
