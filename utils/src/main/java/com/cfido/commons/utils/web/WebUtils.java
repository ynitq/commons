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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.cfido.commons.utils.utils.StringUtils;

public class WebUtils {
	private final static Log log = LogFactory.getLog(WebUtils.class);

	public static String findRealRemoteAddr(HttpServletRequest request) {

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
	 * 或者缩减版的字符串。如果字符串的长度超过了参数的长度，则只截取前len个
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	private static String getStrSummary(String str, int len) {
		if (str == null) {
			return "NULL";
		} else {
			if (str.length() < len) {
				return str;
			} else {
				return String.format("%s...(%d)", str.substring(0, len), str.length());
			}
		}
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
		List<Entry<String, String>> list = new LinkedList<Map.Entry<String, String>>(params.entrySet());
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
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
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
				buff.append(getStrSummary(values[i], 20));
			}
			params.put(name, buff.toString());
		}
		return params;
	}
}
