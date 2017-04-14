package com.cfido.commons.utils.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.cfido.commons.utils.utils.CookieSavedHttpClient;
import com.cfido.commons.utils.utils.HttpUtilException;

/**
 * <pre>
 * CookieSavedHttpClient 测试
 * </pre>
 * 
 * @author 梁韦江 2017年3月14日
 */
public class CookieSavedHttpClientTest {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CookieSavedHttpClientTest.class);

	private final CookieSavedHttpClient client = new CookieSavedHttpClient();

	@Test
	public void doit() throws Exception {
		// this.postLogin();

		// Thread.sleep(200);

		// this.getLogin();

		System.out.println(client.execute("http://192.168.100.10:20040/dev/headers", null, true));
	}

	void getLogin() throws HttpUtilException, IOException {
		String content = client.execute("http://192.168.100.10:20010/", null, false);
		log.debug("get后的内容:{}", content);
	}

	void postLogin() throws Exception {
		Map<String, Object> param = new HashMap<>();
		param.put("openId", 1);

		String content = client.execute("http://192.168.100.10:20010/", param, true);
		log.debug("post后的内容:{}", content);
	}

}
