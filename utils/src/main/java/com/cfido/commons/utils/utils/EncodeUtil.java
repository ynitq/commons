package com.cfido.commons.utils.utils;

/**
 * 处理各类编码的工具
 * 
 * @author liangwj
 * 
 */
public class EncodeUtil {

	public static String input(String in) {
		if (in == null || in.length() == 0) {
			return "";
		}
		char[] charArray = in.toCharArray();
		StringBuffer strBuff = new StringBuffer();

		for (int i = 0; i < charArray.length; i++) {
			switch (charArray[i]) {
			case '\r':
				break;
			case '&':
				strBuff.append("&amp;");
				break;
			case '<':
				strBuff.append("&lt;");
				break;
			case '>':
				strBuff.append("&gt;");
				break;
			case '\"':
				strBuff.append("&quot;");
				break;
			default:
				strBuff.append(charArray[i]);
			}

		}
		return strBuff.toString();
	}

	public static String html(String in, boolean space) {
		if (in == null || in.length() == 0) {
			return "";
		}
		char[] charArray = in.toCharArray();
		StringBuffer strBuff = new StringBuffer();

		for (int i = 0; i < charArray.length; i++) {
			switch (charArray[i]) {
			case '\r':
				break;
			case '&':
				strBuff.append("&amp;");
				break;
			case '<':
				strBuff.append("&lt;");
				break;
			case '>':
				strBuff.append("&gt;");
				break;
			case '\"':
				strBuff.append("&quot;");
				break;
			case '\n':
				strBuff.append("<br/>");
				break;
			case ' ':
			case '\t':
				if (space) {
					strBuff.append(" &nbsp;");
				}
				break;
			default:
				strBuff.append(charArray[i]);
			}

		}
		return strBuff.toString();
	}

	public static String javascript(String in) {
		if (in == null || in.length() == 0) {
			return "";
		}
		char[] charArray = in.toCharArray();
		StringBuffer strBuff = new StringBuffer();

		for (int i = 0; i < charArray.length; i++) {
			switch (charArray[i]) {
			case '\'':
				strBuff.append("\\\'");
				break;
			case '\"':
				strBuff.append("\\\"");
				break;
			case '\n':
				strBuff.append("\\n");
				break;
			case '\r':
				strBuff.append("\\r");
				break;
			case '\t':
				strBuff.append("\\t");
				break;
			default:
				strBuff.append(charArray[i]);
			}

		}
		return strBuff.toString();
	}

	public static String sql(String in) {
		if (in == null || in.length() == 0) {
			return "";
		}
		char[] charArray = in.toCharArray();
		StringBuffer strBuff = new StringBuffer();

		for (int i = 0; i < charArray.length; i++) {
			switch (charArray[i]) {
			case '\\':
				strBuff.append("\\\\");
				break;
			case '\000':
				strBuff.append("\\0");
				break;
			case '\n':
				strBuff.append("\\n");
				break;
			case '\r':
				strBuff.append("\\r");
				break;
			case '\032':
				strBuff.append("\\Z");
				break;
			case '\'':
				strBuff.append("\\'");
				break;
			case '\"':
				strBuff.append("\\\"");
				break;
			default:
				strBuff.append(charArray[i]);
			}

		}
		return strBuff.toString();
	}

	public static String url(String strSrc, String charset) {
		String res = null;
		if (strSrc != null) {
			try {
				res = java.net.URLEncoder.encode(strSrc, charset);
			} catch (java.io.UnsupportedEncodingException ex) {
			}
		}
		return res;
	}

	public static String url(String strSrc) {
		return url(strSrc, "utf-8");
	}
}
