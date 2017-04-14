package com.cfido.commons.utils.utils;

import java.net.IDN;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * email验证工具
 * 
 * @author liangwj
 * 
 */
public class EmailUtil {
	private static String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
	private static String DOMAIN = ATOM + "+(\\." + ATOM + "+)*";
	private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";
	private final Pattern localPattern;
	private final Pattern domainPattern;

	private EmailUtil() {
		this.localPattern = Pattern.compile(ATOM + "+(\\." + ATOM + "+)*", 2);

		this.domainPattern = Pattern.compile(DOMAIN + "|" + IP_DOMAIN, 2);
	}

	private final static EmailUtil instance = new EmailUtil();

	/**
	 * 判断一个字符串是否合法的邮箱
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isEmail(String value) {
		return instance.valid(value);
	}

	public boolean valid(String value) {
		if ((value == null) || (value.length() == 0)) {
			return false;
		}

		String[] emailParts = value.toString().split("@");
		if (emailParts.length != 2) {
			return false;
		}

		if ((emailParts[0].endsWith(".")) || (emailParts[1].endsWith("."))) {
			return false;
		}

		if (!(matchPart(emailParts[0], this.localPattern))) {
			return false;
		}

		return matchPart(emailParts[1], this.domainPattern);
	}

	private boolean matchPart(String part, Pattern pattern) {
		try {
			part = IDN.toASCII(part);
		} catch (IllegalArgumentException e) {
			return false;
		}
		Matcher matcher = pattern.matcher(part);
		return matcher.matches();
	}
}