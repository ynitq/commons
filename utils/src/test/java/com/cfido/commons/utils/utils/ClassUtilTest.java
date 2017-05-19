package com.cfido.commons.utils.utils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.util.Assert;

import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.beans.apiServer.BaseResponse;
import com.cfido.commons.utils.utils.ClassUtil.MethodInfo;

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

		Assert.notNull(date, "应该能获取类文件的时间");
	}

	@Test
	public void testGetClassBuildTimeFromJar() throws Exception {
		log.debug("测试当类在Jar文件时，获取的jar文件的生成时间");

		// jar文件 antlr-2.7.7.jar
		Class<?> clazz = antlr.actions.cpp.ActionLexerTokenTypes.class;

		Date date = ClassUtil.getClassBuildTime(clazz.getName());

		Assert.notNull(date, "应该能获取jar文件的时间");
	}

	public class MyTestCLass extends BaseResponse {

		@AComment("在属性name上的一个注解")
		private String name;

		/** 这个是 getter */
		public String getName() {
			return null;
		}

		public String isBoolean() {
			return null;
		}

		public void getErrorName1() {

		}

		/** 这个是 getter */
		public Boolean getBoolean1() {
			return true;
		}

		/** 这个是 getter */
		public boolean isBoolean1() {
			return true;
		}
	}

	@Test
	public void test_findGetter() {
		log.debug("测试 findGetter方法");

		Class<?> clazz = MyTestCLass.class;

		List<MethodInfo> list = ClassUtil.findGetter(clazz);
		log.debug("在类 {} 中，找到 {} 个 getter", clazz.getName(), list.size());

		Assert.isTrue(list.size() == 7, "共7个getter");
	}

	@Test
	public void test_getAllAnnoFromField() {
		log.debug("测试 getAllAnnoFromField方法");
		Class<?> clazz = MyTestCLass.class;
		Map<String, AComment> map = ClassUtil.getAllAnnoFromField(clazz, AComment.class);

		for (Map.Entry<String, AComment> en : map.entrySet()) {
			log.debug("属性 {} = {}", en.getKey(), en.getValue().value());
		}
	}

}
