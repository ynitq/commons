package com.cfido.commons.utils.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 常用字符串操作工具，比apache common包的好用一点
 * </pre>
 * 
 * @author 梁韦江 2015年5月15日
 */
public class StringUtils {
	private final static char[] hexDigits = "0123456789abcdef".toCharArray();

	/**
	 * 首字母转大写
	 * 
	 * @param str
	 * @return
	 */
	public static String upFirstChar(String str) {
		if (str != null && !str.equals("")) {
			char[] ch = str.toCharArray();
			ch[0] = Character.toUpperCase(ch[0]);
			return new String(ch);
		}
		return str;
	}

	/**
	 * 按驼峰命名方式构建类名
	 * 
	 * @return
	 */
	public static String setClassName(String className) {
		if (StringUtils.isNotEmpty(className) && className.contains("_")) {
			String result = "";
			String[] classNameList = className.split("_");
			for (String s : classNameList) {
				result += upFirstChar(s);
			}
			return result;
		}
		return upFirstChar(className);
	}

	/**
	 * 按驼峰命名方式构建，首字母是小写
	 * 
	 * @return
	 */
	public static String setName(String className) {
		return lowerFirstChar(setClassName(className));
	}

	/**
	 * 字节数组 -- 十六进制字符串
	 * 
	 * @param byteArray
	 * @return
	 */
	public static String byteArray2String(byte[] byteArray) {
		StringBuffer resultSb = new StringBuffer(33);
		for (int i = 0; i < byteArray.length; i++) {

			int value = byteArray[i];
			if (value < 0) {
				value += 256;
			}
			int d1 = value / 16;
			int d2 = value % 16;
			resultSb.append(hexDigits[d1]).append(hexDigits[d2]);
		}
		return resultSb.toString();
	}

	public static boolean isBlank(String str) {
		int strLen;
		if ((str == null) || ((strLen = str.length()) == 0)) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isEmpty(String str) {
		return !hasText(str);
	}

	public static boolean isNotEmpty(String str) {
		return hasText(str);
	}

	/**
	 * MD5, 按utf-8格式编码
	 * 
	 * @param origin
	 * @return
	 */
	public static String md5(String origin) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] byteArray = md.digest(origin.getBytes("utf-8"));
			return byteArray2String(byteArray);
		} catch (Exception ex) {
			return null;
		}
	}

	public static String lowerFirstChar(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		char[] ca = str.toCharArray();
		ca[0] = Character.toLowerCase(ca[0]);
		return new String(ca);
	}

	public static String upperFirstChar(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		char[] ca = str.toCharArray();
		ca[0] = Character.toUpperCase(ca[0]);
		return new String(ca);
	}

	/**
	 * 为目标字符串左补某个字符串到目标长度
	 * 
	 * @param src
	 *            目标字符串
	 * @param c
	 *            左补字符串
	 * @param length
	 *            字符串目标长度
	 * @return
	 */
	public static String fillStringLeft(String src, char c, int length) {
		if (length < src.length()) {
			return src.substring(0, length - 1);
		}
		StringBuffer sb = new StringBuffer(length);
		for (int i = src.length(); i < length; ++i) {
			sb.append(c);
		}
		sb.append(src);
		return sb.toString();
	}

	public static String getMarkString(String Str, String LeftMark, String RightMark) {
		String tmpreturn = Str;
		if (tmpreturn.indexOf(LeftMark, 0) != -1) {
			int LeftMarkPoint = tmpreturn.indexOf(LeftMark, 0) + LeftMark.length();
			int RightMarkPoint = tmpreturn.indexOf(RightMark, LeftMarkPoint);
			if (RightMarkPoint != -1) {
				int ValueLength = RightMarkPoint;
				tmpreturn = tmpreturn.substring(LeftMarkPoint, ValueLength);
			}
		}
		if (tmpreturn == Str) {
			return null;
		} else {
			return tmpreturn;
		}
	}

	public static ArrayList<String> getMarkStringList(String Str, String LeftMark, String RightMark) {
		ArrayList<String> al = new ArrayList<String>();
		String tempstr = new String(Str);
		String tempv = "";
		while (getMarkString(tempstr, LeftMark, RightMark) != null) {
			tempv = getMarkString(tempstr, LeftMark, RightMark);
			if (tempv == null)
				break;
			al.add(new String(tempv));

			tempstr = Pattern.compile(LeftMark + tempv + RightMark, Pattern.LITERAL).matcher(tempstr).replaceFirst("");
		}
		return al;
	}

	private final static String DELIM_START = "${";
	private final static char DELIM_STOP = '}';
	private final static int DELIM_START_LEN = 2;
	private final static int DELIM_STOP_LEN = 1;
	private final static int LOOP_LIMIT = 10;

	/**
	 * <pre>
	 * 在value中搜索${...}字样，并从valueMap中寻找对应的值，并替换，本方法最多搜索10次
	 * 
	 * 例如: 
	 * map.put("key1", "value1");
	 * map.put("key2", " 2----${key1}-----2 ");
	 * map.put("key3", " 3---${key2}---${key2}--3 ");
	 * map.put("key4", " 4-${key3}-4 ");
	 * 
	 * System.out.println(getStringFromMap("key4", map));
	 * 
	 * </pre>
	 * 
	 * @param key
	 * @param valueMap
	 * @return
	 */
	public static String getStringFromMap(String key, IGetStringFromMap valueMap) {
		String value = valueMap.get(key);
		return recursiveReplaceValueFromMap(value, valueMap, 0);
	}

	/**
	 * 在value中搜索${...}字样，并从valueMap中寻找对应的值，并替换，本方法最多搜索10次
	 * 
	 * @param val
	 * @param valueMap
	 * @param loop
	 * @return
	 */
	public static String recursiveReplaceValueFromMap(String val, IGetStringFromMap valueMap, int loop) {
		if (loop > LOOP_LIMIT) {
			// 本方法最多搜索10次
			return val;
		}

		if (val == null) {
			return val;
		}

		StringBuffer sbuf = new StringBuffer();

		int curPostion = 0;
		while (true) {
			int keyBegin = val.indexOf(DELIM_START, curPostion);
			if (keyBegin == -1) {
				if (curPostion == 0) {
					return val;
				}
				sbuf.append(val.substring(curPostion, val.length()));
				return sbuf.toString();
			}

			sbuf.append(val.substring(curPostion, keyBegin));
			int keyEnd = val.indexOf(DELIM_STOP, keyBegin);
			if (keyEnd == -1) {
				// 如果出现非法的情况，就直接返回原值
				return val;
			}

			keyBegin += DELIM_START_LEN;
			String key = val.substring(keyBegin, keyEnd);

			String replacement = valueMap.get(key);

			if (replacement != null) {

				// 递归调用，每次递归时，一定要将次数+1
				String recursiveReplacement = recursiveReplaceValueFromMap(replacement, valueMap, loop + 1);

				sbuf.append(recursiveReplacement);
			}
			curPostion = keyEnd + DELIM_STOP_LEN;
		}
	}

	/**
	 * 在搜索结果中标记关键词
	 * 
	 * @param text
	 * @param keyword
	 * @param htmlClassName
	 * @return
	 */
	public static String markKeyword(String text, String keyword, String htmlClassName) {
		if (StringUtils.isEmpty(text) || StringUtils.isEmpty(keyword)) {
			return text;
		}

		StringBuffer sb = new StringBuffer();
		int last = 0;
		int len = keyword.length();
		while (true) {
			int index = text.indexOf(keyword, last);
			if (index >= 0) {
				sb.append(text.substring(last, index));
				sb.append("<span class='").append(htmlClassName).append("'>").append(keyword).append("</span>");

				last = index + len;
			} else {
				if (last == 0) {
					sb.append(text);
				} else {
					sb.append(text.substring(last));
				}
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 从classpath中，将文件读入到byte[]
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static byte[] loadFileFromClassPath(String fileName) throws IOException {
		// InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
		InputStream is = StringUtils.class.getClassLoader().getResourceAsStream(fileName);
		if (is != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
			byte[] buf = new byte[4096];
			int len;
			while ((len = is.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			byte[] bytes = out.toByteArray();
			return bytes;
		} else {
			return null;
		}
	}

	public static String substring(String value, int i, int length) {
		if (value == null) {
			return null;
		}
		if (value.length() > i + length) {
			return value.substring(i, i + length);
		} else if (value.length() < i) {
			return value.substring(i);
		} else {
			return "";
		}
	}

	/**
	 * 将用逗号分隔的字符串，转换成为set
	 * 
	 * @param str
	 *            将用逗号分隔的字符串
	 * @return set
	 */
	public static Set<String> str2Set(String str) {
		Set<String> set = new HashSet<>();
		if (str != null) {
			String[] ary = str.split(",");
			for (String str1 : ary) {
				if (isNotEmpty(str1)) {
					set.add(str1);
				}
			}
		}
		return set;
	}

	/**
	 * 将set转换成为用逗号分隔的字符串
	 * 
	 * @param set
	 *            字符串set
	 * @return 用逗号分隔的字符串
	 */
	public static String set2Str(Set<String> set) {
		StringBuffer sb = new StringBuffer();

		for (String str : set) {
			if (isNotEmpty(str)) {
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append(str);
			}
		}

		return sb.toString();
	}

	public static String randomUUID() {
		String str = UUID.randomUUID().toString();
		return str.replace("-", "");
	}

	/**
	 * 过滤掉中间的空白字符，多个变成一个，例如："a\t\n \t\t \n\nb" 过滤后成为 "a b",只保留一个空格
	 */
	public static String trimMiddleWhitespace(String str) {
		StringBuffer sb = new StringBuffer();

		boolean skip = false;
		for (char c : str.toCharArray()) {
			if (Character.isWhitespace(c)) {
				if (!skip) {
					sb.append(' ');
				}
				skip = true;
			} else {
				sb.append(c);
				skip = false;
			}
		}
		return sb.toString();
	}

	/**
	 * 将数据库名、表名等带下划线的名字变成驼峰结构的
	 * 
	 * @param s
	 *            表名，例如 "app_user"
	 * @param upFristChar
	 *            是否首字母大写
	 * @return 返回首字母大写的类名或者首字母小写的属性名
	 */
	public static String toUpperCamelCase(String s, boolean upFristChar) {
		if (s == null) {
			return s;
		}

		boolean capitalize = upFristChar;
		char[] charArray = s.toCharArray();

		StringBuffer result = new StringBuffer();
		for (char c : charArray) {
			if (c == '_' || Character.isWhitespace(c) || c == '-') {
				capitalize = true;
				continue;
			}

			if (capitalize) {
				result.append(Character.toUpperCase(c));
				capitalize = false;
			} else {
				result.append(c);
			}
		}

		if (result.length() > 0) {
			// 设置首字母大小写
			if (upFristChar) {
				result.setCharAt(0, Character.toUpperCase(result.charAt(0)));
			} else {
				result.setCharAt(0, Character.toLowerCase(result.charAt(0)));
			}
		}

		String r = result.toString();
		return r;
	}

	/**
	 * 与spring的springUtil同名方法，方便日后直接使用spring的工具集
	 * 
	 * @return
	 */
	public static boolean hasText(String str) {
		return org.springframework.util.StringUtils.hasText(str);
	}

	private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
	private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
	private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
	private static final String regEx_space = "\\s*|\t|\r|\n";// 定义空格回车换行符

	/**
	 * 删除Html标签
	 */
	public static String delHTMLTag(String htmlStr) {
		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签

		Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
		Matcher m_space = p_space.matcher(htmlStr);
		htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
		return htmlStr.trim(); // 返回文本字符串
	}
}
