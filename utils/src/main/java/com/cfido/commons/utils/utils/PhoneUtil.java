package com.cfido.commons.utils.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * <pre>
 * 手机号码工具
 * </pre>
 * 
 * @author 梁韦江
 */
public class PhoneUtil {

	private final static PhoneUtil instance = new PhoneUtil();

	Matcher phoneMatcher;

	private final Pattern phoneRegex;

	private PhoneUtil() {
		this.phoneRegex = Pattern
				.compile("^(((17[0-9])|(13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");

	}

	/**
	 * 验证手机号码
	 */
	public static boolean isValidPhone(String phone) {
		if (StringUtils.hasText(phone)) {
			Matcher matcher = instance.phoneRegex.matcher(phone);
			return matcher.matches();
		}
		return false;
	}

}
