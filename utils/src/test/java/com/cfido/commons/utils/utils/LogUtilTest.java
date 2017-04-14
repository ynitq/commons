package com.cfido.commons.utils.utils;

import org.junit.Test;

import com.cfido.commons.beans.apiExceptions.SimpleApiException;
import com.cfido.commons.utils.utils.LogUtil;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author 梁韦江
 * 2016年9月8日
 */
public class LogUtilTest {

	@Test
	public void test() {
		try {
			m1();
		} catch (Exception e) {
			System.out.println(LogUtil.getTraceString(null, e));
			System.out.println("----------");
			System.out.println(LogUtil.getTraceStringOld(null, e));
		}
	}

	private void m1() throws Exception {
		try {
			m2();
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private void m2() throws Exception {
		throw new SimpleApiException("测试");
	}

}
