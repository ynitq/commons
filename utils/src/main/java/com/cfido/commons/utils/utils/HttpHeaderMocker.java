package com.cfido.commons.utils.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * <pre>
 * 模拟http请求时的header
 * </pre>
 * 
 * @author 梁韦江 2017年3月14日
 */
public class HttpHeaderMocker {

	private static HttpHeaderMocker instance = new HttpHeaderMocker();

	/** 访问页面时用的浏览器的类型，每次访问时，随机个一个 */
	private final ArrayList<String> userAgentList = new ArrayList<>();

	private final Map<String, String> commonHttpHeads = new HashMap<>();

	private HttpHeaderMocker() {
		this.buildUserAgent();
		this.buildCommons();
	}

	/**
	 * 常见的headers
	 * 
	 * <pre>
	 * 已经排除了httpClient本身会自动增加的header，包括以下:
	 * content-type:	application/x-www-form-urlencoded; charset=UTF-8
	 * connection:	Keep-Alive
	 * host:
	 * </pre>
	 */
	private void buildCommons() {
		commonHttpHeads.put("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		commonHttpHeads.put("Accept-Encoding", "gzip, deflate");
		commonHttpHeads.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
		commonHttpHeads.put("Cache-Control", "max-age=0");
		commonHttpHeads.put("Upgrade-Insecure-Requests", "1");
	}

	/**
	 * 常见的浏览器标识
	 */
	private void buildUserAgent() {

		userAgentList.add(
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
		userAgentList.add(
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		userAgentList.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1");
		userAgentList.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0");
		userAgentList.add(
				"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 10.0; Win64; x64; Trident/7.0; .NET4.0C; .NET4.0E; .NET CLR 2.0.50727; .NET CLR 3.0.30729; .NET CLR 3.5.30729)");
		userAgentList.add(
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.79 Safari/537.36 Edge/14.14393");
	}

	private final Random random = new Random();

	private String getOne() {
		int index = this.random.nextInt(this.userAgentList.size());
		return this.userAgentList.get(index);
	}

	/**
	 * 随机返回一个浏览器的类型
	 */
	public static String randomUserAgent() {
		return instance.getOne();
	}

	/**
	 * 增加常见的头信息
	 * 
	 * @param method
	 */
	public static void addCommonHeaders(HttpRequestBase method) {
		Set<Map.Entry<String, String>> set = instance.commonHttpHeads.entrySet();
		for (Entry<String, String> en : set) {
			method.addHeader(en.getKey(), en.getValue());
		}
	}

}
