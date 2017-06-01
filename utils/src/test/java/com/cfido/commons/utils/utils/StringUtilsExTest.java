package com.cfido.commons.utils.utils;

import org.junit.Test;
import org.springframework.util.Assert;

/**
 * <pre>
 * StringUtilsEx 的测试用例
 * </pre>
 * 
 * @author 梁韦江 2017年5月23日
 */
public class StringUtilsExTest {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StringUtilsExTest.class);

	@Test
	public void test_str2array() {
		log.debug("测试 str2array");
		String str = "1,2,3";
		Integer[] ary = StringUtilsEx.str2array(str);

		Assert.notNull(ary, "not null");
		Assert.isTrue(ary.length == 3, "数组长度为3");
	}

	@Test
	public void test_arry2Str() {
		log.debug("测试 arry2Str");
		Integer[] ary = new Integer[] {
				1, 2, 3
		};

		String str = StringUtilsEx.arry2Str(ary);
		Assert.hasText(str, "hasText");
		Assert.isTrue("1,2,3".equals(str), "输出应该是：1,2,3");
	}

	@Test
	public void test_delHTMLTag() {
		log.debug("测试 delHTMLTag");
		this.delHTMLTag("");
		this.delHTMLTag("aa<bb");
		this.delHTMLTag("aa<bb>111");
		this.delHTMLTag("aa<bb>11<22");
		this.delHTMLTag("aa<bb>11>22");
	}

	private void delHTMLTag(String str) {
		String res = StringUtilsEx.delHTMLTag(str);
		log.debug("delHTMLTag( {} ) = {}", str, res);
	}

}
