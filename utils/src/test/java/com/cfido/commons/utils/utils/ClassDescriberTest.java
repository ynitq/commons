package com.cfido.commons.utils.utils;

import org.junit.Test;

import com.cfido.commons.beans.apiServer.impl.CommonSuccessResponse;

/**
 * <pre>
 * ClassDescriber的测试用例
 * </pre>
 * 
 * @author 梁韦江 2017年5月9日
 */
public class ClassDescriberTest {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClassDescriberTest.class);

	@Test
	public void testCreate() {
		log.debug("测试 ClassDescriber.create()");

		Class<?> clazz = CommonSuccessResponse.class;

		String str = ClassDescriber.create(clazz);

		log.debug("分析 {} 的结果是：\n {}", clazz.getName(), str);
	}

}
