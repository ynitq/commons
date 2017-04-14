package com.cfido.commons.utils.utils;

import org.junit.Assert;
import org.junit.Test;

import com.cfido.commons.utils.utils.EncryptException;
import com.cfido.commons.utils.utils.EncryptUtil;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author 梁韦江
 *  2016年7月6日
 */
public class EncryptUtilTest {

	private final static String DES_KEY = "我们都是好人";

	@Test
	public void test() throws EncryptException {

		String src = "{xxx:xxx}";

		String ascll = EncryptUtil.desEncryptAscllString(DES_KEY, src);
		Assert.assertNotNull("加密的结果不应该为null", ascll);

		String str = EncryptUtil.desDecryptAscll(DES_KEY, ascll);
		Assert.assertNotNull("解密的结果不应该为null", ascll);

		Assert.assertEquals("解密后的字符串该和原来的一样", src, str);

	}

}
