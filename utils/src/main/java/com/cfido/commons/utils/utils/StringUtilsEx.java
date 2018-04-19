package com.cfido.commons.utils.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * <pre>
 * 扩展字符串操作工具集，我们常用的是Spring的StringUtil
 * 
 * 和Spring重复了好多方法：
 * 
 * 首字母大写 : {@linkplain org.springframework.util.StringUtils#capitalize(String)}
 * 首字母小写 : {@linkplain org.springframework.util.StringUtils#uncapitalize(String)}
 * </pre>
 * 
 * @author 梁韦江 2015年5月15日
 */
public class StringUtilsEx {

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
		ArrayList<String> al = new ArrayList<>();
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

		int curPosition = 0;
		while (true) {
			int keyBegin = val.indexOf(DELIM_START, curPosition);
			if (keyBegin == -1) {
				if (curPosition == 0) {
					return val;
				}
				sbuf.append(val.substring(curPosition, val.length()));
				return sbuf.toString();
			}

			sbuf.append(val.substring(curPosition, keyBegin));
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
			curPosition = keyEnd + DELIM_STOP_LEN;
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
		if (!StringUtils.hasText(text) || !StringUtils.hasText(keyword)) {
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
				if (StringUtils.hasText(str1)) {
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
			if (StringUtils.hasText(str)) {
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append(str);
			}
		}

		return sb.toString();
	}

	/**
	 * 将set转换成为用逗号分隔的字符串
	 * 
	 * @param ary
	 *            字符串set
	 * @return 用逗号分隔的字符串
	 */
	public static String arry2Str(Integer[] ary) {
		StringBuffer sb = new StringBuffer();

		if (ary != null) {
			for (Integer str : ary) {
				if (str != null) {
					if (sb.length() > 0) {
						sb.append(',');
					}
					sb.append(str);
				}
			}
		}

		return sb.toString();
	}

	/**
	 * 将字符串解析为整形数组
	 * 
	 * @param str
	 * @return
	 */
	public static Integer[] str2array(String str) {
		List<Integer> list = new LinkedList<>();
		if (StringUtils.hasText(str)) {
			StringTokenizer st = new StringTokenizer(str, ",");
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				try {
					Integer value = Integer.parseInt(s);
					list.add(value);
				} catch (NumberFormatException e) {
				}
			}
		}
		return list.toArray(new Integer[0]);
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
	 * 删除Html标签
	 */
	public static String delHTMLTag(String val) {
		if (val == null) {
			return val;
		}

		StringBuilder sbuf = new StringBuilder();

		int curPosition = 0;
		while (true) {
			// 先找 < 的位置
			int keyBegin = val.indexOf("<", curPosition);
			if (keyBegin == -1) {
				// 如果已经找不到 < 了，就可以返回结果了
				if (curPosition == 0) {
					// 如果当前指针在头部，就说明一次都没找到，可直接返回，
					sbuf.append(val);
				} else {
					// 如果不在头部，就需要将指针以后的部分都加进来
					sbuf.append(val.substring(curPosition, val.length()));
				}

				// 找不到就可以退出循环了
				break;
			} else {
				// 如果能找到 < ，就先将指针到 < 之间的都加到buf
				sbuf.append(val.substring(curPosition, keyBegin));

				// 接着找 >的位置
				int keyEnd = val.indexOf(">", keyBegin);
				if (keyEnd == -1) {
					// 如果找不到>结束，将剩下部分加进来，然后退出循环
					sbuf.append(val.substring(keyBegin));
					break;
				} else {
					// 如果能找到>,就将指针跳到>之后，略过<>包含的内容
					curPosition = keyEnd + 1;
				}
			}
		}

		// 返回前顺手将>都删除
		return sbuf.toString();
	}

	/**
	 * 获取缩减版的字符串。如果字符串的长度超过了参数的长度，则只截取前len个
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public static String getStrSummary(String str, int len) {
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

}
