package com.cfido.commons.utils.ipSeeker.qqwry;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.cfido.commons.utils.utils.LogUtil;

/**
 * 中国的省
 * 
 * @author liangwj
 * 
 */
public class ChinaProvince {

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ChinaProvince.class);

	private static ChinaProvince instance;

	private static synchronized ChinaProvince getInstance() {
		if (instance == null) {
			instance = new ChinaProvince();
			instance.init();
		}
		return instance;
	}

	private static String MAP_FILE = "ChinaProvince.txt";

	private final Set<String> set = new HashSet<String>();
	private final Map<String, String> otherMap = new HashMap<String, String>();
	private final Set<Integer> otherMapLenSet = new HashSet<Integer>();

	/**
	 * 从ChinaProvince.txt文件中初始化特殊的名字
	 */
	private void initOtherMap() {
		InputStream pydata = this.getClass().getResourceAsStream(MAP_FILE);
		if (pydata == null) {
			log.warn(String.format("找不到地名和省市的对照文件：%s", MAP_FILE));
			return;
		}
		int lineNo = 0;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(pydata, "UTF8"));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line != null) {
					String sa[] = line.split("=");
					if (sa.length == 2) {
						String key = sa[0].trim();
						this.otherMap.put(key, sa[1].trim());
						this.otherMapLenSet.add(key.length());
					}
				}
				lineNo++;
			}
		} catch (Exception e) {
			LogUtil.traceWarn(log, "从ChinaProvince.txt初始化省份名字特殊对照表时发生了错误, 行号:" + lineNo);
		}
	}

	/**
	 * 初始化省份
	 */
	private void initMainSet() {
		set.add("北京");
		set.add("天津");
		set.add("上海");
		set.add("重庆");
		set.add("河北");
		set.add("河南");
		set.add("云南");
		set.add("辽宁");
		set.add("黑龙江");
		set.add("湖南");
		set.add("安徽");
		set.add("山东");
		set.add("新疆");
		set.add("江苏");
		set.add("浙江");
		set.add("江西");
		set.add("湖北");
		set.add("广西");
		set.add("甘肃");
		set.add("山西");
		set.add("内蒙古");
		set.add("陕西");
		set.add("吉林");
		set.add("福建");
		set.add("贵州");
		set.add("广东");
		set.add("青海");
		set.add("西藏");
		set.add("四川");
		set.add("宁夏");
		set.add("海南");
		set.add("台湾");
		set.add("香港");
		set.add("澳门");
	}

	private ChinaProvince() {
	}

	private void init() {
		this.initMainSet();
		this.initOtherMap();
	}

	private String getProvince(String name) {
		if (name == null) {
			throw new IllegalArgumentException("原始名字不能为空");
		}

		if (name.length() <= 2) {
			return name;
		}

		if (name.length() >= 2) {
			String str = name.substring(0, 2);
			if (this.set.contains(str)) {
				return str;
			}
		}

		if (name.length() >= 3) {
			String str = name.substring(0, 3);
			if (this.set.contains(str)) {
				return str;
			}
		}

		for (Integer len : this.otherMapLenSet) {
			if (len > 1 && name.length() >= len) {
				String key = name.substring(0, len);
				String value = this.otherMap.get(key);
				if (value != null) {
					return value;
				}
			}
		}

		return name;
	}

	public static String getProvinceName(String name) {
		return getInstance().getProvince(name);
	}

}
