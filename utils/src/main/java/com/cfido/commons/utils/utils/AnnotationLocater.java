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
 * @author 梁韦江 2016年6月29日
 */
public class AnnotationLocater {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AnnotationLocater.class);

	@SafeVarargs
	public static List<Class<?>> getClassList(String packagePrefix, Class<? extends Annotation>... clazzs) throws IOException {

		if (clazzs == null || clazzs.length == 0) {
			return null;
		}

		StringBuilder names = new StringBuilder();
		for (int i = 0; i < clazzs.length; i++) {
			names.append(clazzs[i].getName()).append(" ");
		}

		log.info("扫描带有注解 {} 的类", names.toString());

		List<Class<?>> classes = new LinkedList<>();
		List<String> resourcelist = ResourceScaner.scan(packagePrefix);

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		for (String name : resourcelist) {

			String className = name.replace('/', '.');
			className = className.substring(0, className.length() - 6);
			try {
				Class<?> c = classLoader.loadClass(className);

				for (int i = 0; i < clazzs.length; i++) {
					if (c.getAnnotation(clazzs[i]) != null) {
						classes.add(c);
						break;
					}
				}
			} catch (ClassNotFoundException e) {
			}
		}

		return classes;
	}
}
