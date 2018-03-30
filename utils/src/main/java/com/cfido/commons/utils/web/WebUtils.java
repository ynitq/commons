package com.cfido.commons.utils.web;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.cfido.commons.utils.utils.StringUtilsEx;

/**
 * <pre>
 * 常用的和request和response相关的工具
 * </pre>
 * 
 * @author 梁韦江
 * 
 */
public class WebUtils {
	private final static Log log = LogFactory.getLog(WebUtils.class);

	/**
	 * 获取访问者的ip
	 */
	public static String findRealRemoteAddr(HttpServletRequest request) {
		Assert.notNull(request, "request 参数不能为空");

		String ip = request.getHeader("x-real-ip");
		if (StringUtils.isEmpty(ip)) {
			ip = request.getHeader("x-forwarded-for");
		}
		if (StringUtils.isEmpty(ip)) {
			ip = request.getRemoteAddr();
		}

		if (log.isDebugEnabled()) {
			log.debug("x-real-ip:" + request.getHeader("x-real-ip"));
			log.debug("x-forwarded-for:" + request.getHeader("x-forwarded-for"));
			log.debug("ip:" + ip);
		}

		return ip;
	}

	/**
	 * 将request中的信息输出到一个buff
	 * 
	 * @param request
	 * @param buff
	 */
	public static void debugRequest(HttpServletRequest request, StringBuffer buff) {
		if (request == null) {
			return;
		}

		Map<String, String> params = getParamsFromRequest(request);
		List<Entry<String, String>> list = new LinkedList<>(params.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
			@Override
			public int compare(Entry<String, String> o1, Entry<String, String> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		for (Entry<String, String> en : list) {
			buff.append("\n\t");
			buff.append(en.getKey()).append(" = ").append(en.getValue());
		}

		if (request instanceof StandardMultipartHttpServletRequest) {

			StandardMultipartHttpServletRequest mreq = (StandardMultipartHttpServletRequest) request;

			Map<String, MultipartFile> fileMap = mreq.getFileMap();
			for (Entry<String, MultipartFile> en : fileMap.entrySet()) {
				buff.append("\n\t有上传文件 ");
				buff.append(en.getKey()).append(" = ");

				MultipartFile file = en.getValue();
				if (file.isEmpty()) {
					buff.append("空");
				} else {

					buff.append(file.getOriginalFilename());
					buff.append(" size=").append(file.getSize());
				}
			}
		}
	}

	/**
	 * 从http中获得所有的参数，如果一次参数有多个值，将多个值合并成为一个值，用逗号分开
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getParamsFromRequest(HttpServletRequest request) {
		Assert.notNull(request, "request 参数不能为空");

		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<>();
		Enumeration<String> e = request.getParameterNames();

		while (e.hasMoreElements()) {
			String name = e.nextElement();
			String[] values = request.getParameterValues(name);

			StringBuffer buff = new StringBuffer();
			boolean first = true;
			for (int i = 0; i < values.length; i++) {
				if (!first) {
					buff.append(",");
				} else {
					first = false;
				}
				buff.append(StringUtilsEx.getStrSummary(values[i], 20));
			}
			params.put(name, buff.toString());
		}
		return params;
	}

	/**
	 * 获取 http://mydomain.com:7000
	 */
	public static String getSchemeAndServerName(HttpServletRequest request) {
		Assert.notNull(request, "request 参数不能为空");

		StringBuffer url = new StringBuffer();
		String scheme = request.getScheme();
		int port = request.getServerPort();

		if (port < 0) {
			if (scheme.equals("http")) {
				port = 80;
			} else {
				port = 443;
			}
		}

		// http或者https
		url.append(scheme);
		url.append("://");

		url.append(request.getServerName());// xxx.com

		if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
			// 如果不是默认端口，需要加上端口
			url.append(':');
			url.append(port);
		}
		return url.toString();

	}

	/**
	 * 获得全路径
	 */
	public static String getFullPath(HttpServletRequest request, String path) {
		Assert.notNull(request, "request 参数不能为空");

		StringBuilder url = new StringBuilder();

		// https://xxx.com:7080
		url.append(getSchemeAndServerName(request));

		// 增加 contextPath
		String contextPath = request.getContextPath();
		if (StringUtils.hasText(contextPath)) {
			url.append(contextPath);
		}

		// 补充后面的路径
		if (StringUtils.hasText(path)) {
			if (!path.startsWith("/")) {
				url.append('/');
			}
			url.append(path);
		}
		return url.toString();
	}

	/**
	 * 为ajax 设置跨域的头
	 */
	public static void addCrossDomainHeader(HttpServletResponse response, HttpServletRequest request) {
		Assert.notNull(request, "request 参数不能为空");
		Assert.notNull(response, "response 参数不能为空");

		/** Ajax跨域header */
		response.setHeader("Cache-Control", "no-cache");
//		response.setHeader("Access-Control-Allow-Origin", getSchemeAndServerName(request));
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
	}

	/**
	 * 获得全路径
	 * 
	 * @param needQueryString
	 *            是否需要加上 ?后面的内容
	 * @return
	 */
	public static String getRequestURL(HttpServletRequest request, boolean needQueryString) {
		Assert.notNull(request, "request 参数不能为空");

		if (request != null) {
			StringBuilder sb = new StringBuilder(request.getRequestURL());

			if (needQueryString) {

				String queryString = request.getQueryString();
				if (StringUtils.hasText(queryString)) {
					sb.append("?").append(queryString);
				}
			}

			return sb.toString();
		}
		return null;

	}

}
