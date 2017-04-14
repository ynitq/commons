package com.cfido.commons.utils.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 中文判断及转换工具
 * </pre>
 * 
 * @author 黄云 2016年1月19日
 */
public class ChineseUtil {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChineseUtil.class);

	/**
	 * 判断是否是中文
	 * @param c
	 * @return
	 */
	private static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否是乱码
	 * @param strName
	 * @return
	 */
	public static boolean isMessyCode(String strName) {
		Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
		Matcher m = p.matcher(strName);
		String after = m.replaceAll("");
		String temp = after.replaceAll("\\p{P}", "");
		char[] ch = temp.trim().toCharArray();
		float chLength = 0;
		float count = 0;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!Character.isLetterOrDigit(c)) {
				if (!isChinese(c)) {
					count = count + 1;
				}
				chLength++;
			}
		}
		float result = count / chLength;
		if (result > 0.4) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 转化为中文
	 * @param msg
	 * @return
	 */
	public static String toChinese(String msg) {
		if(StringUtils.isEmpty(msg)){
			return msg;
		}
		if (isMessyCode(msg)) {
			try {
				return new String(msg.getBytes("ISO8859-1"), "UTF-8");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return msg;
	}

}
