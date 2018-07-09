package com.cfido.commons.spring.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cfido.commons.utils.web.WebUtils;

/**
 * <pre>
 * 各种ContextHolder的帮助类
 * </pre>
 * 
 * @author 梁韦江 2016年10月17日
 */
public class WebContextHolderHelper {

	public static String getBasePath() {
		return WebUtils.getFullPath(getRequest(), null);
	}

	public static String getFullPath(String path) {
		return WebUtils.getFullPath(getRequest(), path);
	}

	public static String getBasePathNotHttp() {
		return WebUtils.getServerName(getRequest());
	}

	/**
	 * 获得用户的ip
	 * 
	 * @return
	 */
	public static String getRemoteIp() {
		HttpServletRequest request = getRequest();
		if (request != null) {
			return WebUtils.findRealRemoteAddr(request);
		} else {
			return null;
		}
	}

	/**
	 * 从 ThreadLocal 中 获取 HttpServletRequest
	 */
	public static HttpServletRequest getRequest() {
		ServletRequestAttributes sra = getServletRequestAttributes();
		if (sra != null) {
			return sra.getRequest();
		} else {
			return null;
		}
	}

	/**
	 * 获得全路径
	 * 
	 * @param needQueryString
	 *            是否需要加上 ?后面的内容
	 * @return
	 */
	public static String getRequestURL(boolean needQueryString) {

		HttpServletRequest request = getRequest();
		if (request != null) {
			return WebUtils.getRequestURL(request, needQueryString);
		}
		return null;
	}

	/**
	 * 从 ThreadLocal 中 获取 HttpServletResponse
	 */
	public static HttpServletResponse getResponse() {
		ServletRequestAttributes sra = getServletRequestAttributes();
		if (sra != null) {
			return sra.getResponse();
		} else {
			return null;
		}
	}

	private static ServletRequestAttributes getServletRequestAttributes() {
		ServletRequestAttributes res = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return res;
	}

	/**
	 * 从session中获得内容
	 * 
	 * @param name
	 *            session中的名字
	 * @param clazz
	 *            压转换的类型
	 * @return 值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSessionAttribute(String name, Class<T> clazz) {
		HttpServletRequest request = getRequest();
		if (request != null) {
			Object obj = request.getSession().getAttribute(name);
			if (obj != null) {
				return (T) obj;
			}
		}
		return null;
	}

	/**
	 * 将内容保存到session中
	 * 
	 * @param name
	 *            名字
	 * @param value
	 *            值
	 */
	public static void saveToSession(String name, Object value) {
		HttpServletRequest request = getRequest();
		if (request != null) {
			if (StringUtils.hasText(name) && value != null) {
				request.getSession().setAttribute(name, value);
			}
		}
	}

}
