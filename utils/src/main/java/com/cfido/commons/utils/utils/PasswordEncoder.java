package com.cfido.commons.utils.utils;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.cfido.commons.beans.exceptions.security.InvalidPasswordException;

/**
 * <pre>
 * 密码生成器和校验器，使用MD5加密
 * </pre>
 * 
 * @author 梁韦江 2016年12月1日
 */
public class PasswordEncoder {
	/** 对密码进行编码时，额外的key */
	private static final String PASSWORD_ENCODE_KEY = "\t梁韦江是个好人";

	/**
	 * 对密码进行编码
	 * 
	 * @param password
	 * @return
	 */
	public static String encodePassword(String password) {
		Assert.hasText(password,"密码不能为空");
		return EncryptUtil.md5(password + PASSWORD_ENCODE_KEY);
	}

	/**
	 * 和表单中的密码进行校验
	 * 
	 * @param passwordInForm
	 *            输入的密码
	 * @param passwordEncoded
	 *            加密后的密码
	 * @throws InvalidPasswordException
	 *             如果密码不对就抛错
	 */
	public static void checkPassword(String passwordInForm, String passwordEncoded) throws InvalidPasswordException {
		if (StringUtils.hasText(passwordEncoded) && StringUtils.hasText(passwordInForm)) {
			// 如果两个密码都不为空
			String expected = encodePassword(passwordInForm);
			if (expected.equalsIgnoreCase(passwordEncoded)) {
				// 如果密码相等就直接返回
				return;
			}
		}
		// 密码为空或者密码不对就抛错
		throw new InvalidPasswordException();
	}
}
