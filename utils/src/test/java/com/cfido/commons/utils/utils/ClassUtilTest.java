package com.cfido.commons.utils.utils;

import java.util.Date;

import org.junit.Test;
import org.springframework.util.Assert;

import com.cfido.commons.utils.utils.ClassUtil;

/**
 * <pre>
 * ClassUtil的测试用例
 * </pre>
 * 
 * @author 梁韦江 2017年2月9日
 */
public class ClassUtilTest {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClassUtilTest.class);

	@Test
	public void testGetClassBuildTimeFromFile() throws Exception {
		log.debug("测试当类是文件时，获取的类的build时间");

		Class<?> clazz = ClassUtil.class;

		Date date = ClassUtil.getClassBuildTime(clazz.getName());

		Assert.notNull(date,"应该能获取类文件的时间");
	}

	@Test
	public void testGetClassBuildTimeFromJar() throws Exception {
		log.debug("测试当类在Jar文件时，获取的jar文件的生成时间");

		// jar文件 antlr-2.7.7.jar
		Class<?> clazz = antlr.actions.cpp.ActionLexerTokenTypes.class;

		Date date = ClassUtil.getClassBuildTime(clazz.getName());

		Assert.notNull(date,"应该能获取jar文件的时间");
	}

}
