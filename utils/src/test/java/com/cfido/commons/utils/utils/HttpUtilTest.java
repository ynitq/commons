package com.cfido.commons.utils.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.cfido.commons.beans.apiServer.impl.CommonSuccessResponse;
import com.cfido.commons.utils.utils.HttpUtil;
import com.cfido.commons.utils.utils.HttpUtilException;

/**
 * <pre>
 * http util 测试
 * </pre>
 * 
 * @author 梁韦江
 *  2016年7月21日
 */
public class HttpUtilTest {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HttpUtilTest.class);

	@Test
	public void testJson() throws IOException, HttpUtilException {
		String url = "http://localhost:8080/json";

		CommonSuccessResponse res = HttpUtil.requestJson(CommonSuccessResponse.class, url, null, false, null);
		log.debug("post {} 的结果:\n{}", url, res.getDebugMsg());

	}

	public void testPostFile() throws IOException, HttpUtilException {
		String url = "http://localhost:8080/guest/upload";

		File file = new File("d:/my.pac");
		org.springframework.util.Assert.isTrue(file.exists(),"要上传的测试文件不存在");

		String res = HttpUtil.postFileUseHttpClient(url, file, "file", null);
		log.debug("post {} 的结果:\n{}", url, res);

	}

	public void testHttpsGet() throws HttpUtilException, IOException {
		String url = "https://www.baidu.com/";
		String res = HttpUtil.getInstance().doExecute(url, null, false, null);

		log.debug("获取 {} 的结果:\n{}", url, res);
	}

	public void testPost() throws IOException, HttpUtilException {
		String url = "http://localhost:8080/p1";
		Map<String, Object> map = new HashMap<>();
		map.put("name1", "value1");
		map.put("name2", "value2");

		String res = HttpUtil.request(url, map, true, null);
		log.debug("post {} 的结果:\n{}", url, res);
	}

}
