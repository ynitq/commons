package com.cfido.commons.utils.utils;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cfido.commons.annotation.api.AClass;
import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.utils.utils.beans.BeanWithACommentAnno;

/**
 * <pre>
 * AnnotationLocater 测试用例
 * </pre>
 * 
 * @author 梁韦江 2016年9月28日
 */
public class AnnotationLocaterTest {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AnnotationLocaterTest.class);

	@Test
	public void test() throws IOException {
		String prefix = BeanWithACommentAnno.class.getPackage().getName();

		List<Class<?>> scanResult = AnnotationLocater.getClassList(prefix, AComment.class, AClass.class);

		log.debug("测试扫描 带指定注解 的类：扫描结果:{}", this.printScanResultToStr(scanResult));

		Assert.assertTrue("应该能扫描到两个", scanResult.size() == 2);
	}

	private String printScanResultToStr(List<Class<?>> scanResult) {
		StringBuffer sb = new StringBuffer();
		for (Class<?> clazz : scanResult) {
			sb.append('\n');
			sb.append(clazz.getName());
		}
		return sb.toString();
	}

}
