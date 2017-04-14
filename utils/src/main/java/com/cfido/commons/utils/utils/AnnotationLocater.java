package com.cfido.commons.utils.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

/**
 * <pre>
 * 扫描指定包（包括jar）下的class文件
 * </pre>
 * 
 * @author 梁韦江
 *  2016年6月29日
 */
public class AnnotationLocater {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AnnotationLocater.class);

	public static List<Class<?>> getClassList(String packagePrefix, Class<? extends Annotation> clazz) throws IOException {
		log.info("search " + clazz.getName());

		List<Class<?>> classes = new LinkedList<>();
		List<String> resourcelist = ResourceScaner.scan(packagePrefix);

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		for (String name : resourcelist) {

			String className = name.replace('/', '.');
			className = className.substring(0, className.length() - 6);
			try {
				Class<?> c = classLoader.loadClass(className);
				if (c.getAnnotation(clazz) != null) {
					classes.add(c);
				}
			} catch (ClassNotFoundException e) {
			}
		}

		return classes;
	}
}
