package com.cfido.commons.utils.web;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

import com.cfido.commons.utils.utils.LogUtil;

/**
 * web项目中，要从session检查的用户对象的基类
 * 
 * @author liangwj
 * 
 */
public abstract class AbstractWebUser {
	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(AbstractWebUser.class);

	public abstract boolean checkRights(int optId);

	public abstract String getUserName();

	private String ip;

	public String getIp() {
		return ip;
	}

	public void updateIp(HttpServletRequest request) {
		this.ip = WebUtils.findRealRemoteAddr(request);
	}

	/**
	 * 将权限字符串变成权限的Set
	 * 
	 * @param rightStr
	 * @return
	 */
	public static Set<Integer> strToSet(String rightStr) {
		Set<Integer> res = new HashSet<>();

		if (StringUtils.hasText(rightStr)) {
			String[] sa = rightStr.split(",");
			for (String str : sa) {
				if (StringUtils.hasText(str)) {
					try {
						Integer optId = Integer.parseInt(str);
						res.add(optId);
					} catch (Exception e) {
						LogUtil.traceError(log, e);
					}
				}
			}
		}
		return res;
	}

	/**
	 * 将权限set变回成为字符串，用于存盘
	 * 
	 * @param set
	 * @return
	 */
	public static String setToStr(Set<Integer> set) {
		StringBuffer sb = new StringBuffer(100);
		if (set != null) {
			for (Integer i : set) {
				if (sb.length() != 0) {
					sb.append(",");
				}
				sb.append(i);
			}
		}
		return sb.toString();
	}
}
