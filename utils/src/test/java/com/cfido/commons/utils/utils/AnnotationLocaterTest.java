package com.cfido.commons.utils.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.Test;
import org.springframework.stereotype.Service;

import com.cfido.commons.utils.utils.AnnotationLocater;

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
		String prefix = "com.linzi.framework.sortedLock";

		Class<? extends Annotation> annoClass = Service.class;

		List<Class<?>> scanResult = AnnotationLocater.getClassList(prefix, annoClass);

		log.debug("测试扫描 带注解 {} 的类：扫描结果:{}", annoClass.getName(), this.printScanResultToStr(scanResult));
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
