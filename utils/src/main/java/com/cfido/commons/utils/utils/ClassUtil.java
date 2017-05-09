package com.cfido.commons.utils.utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.jar.JarFile;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;

import org.springframework.util.StringUtils;

/**
 * <pre>
 * 分析class结构的工具，主要和泛型相关
 * </pre>
 * 
 * @author 梁韦江 2015年5月15日
 */
public class ClassUtil {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClassUtil.class);

	/**
	 * 返回xmlbean类接口中工厂类中指定的方法
	 * 
	 * @param clazz
	 * @param methodName
	 * @param paramClass
	 * @return
	 */
	public static Method getXmlBeanFactoryMethod(Class<?> clazz, String methodName, Class<?> paramClass) {
		if (clazz == null) {
			return null;
		}
		try {
			for (Class<?> factoryClass : clazz.getDeclaredClasses()) {
				if (factoryClass.getName().endsWith("$Factory")) {
					return factoryClass.getMethod(methodName, paramClass);
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获得某个类的泛型的类
	 * 
	 * <pre>
	 * 如果没定义泛型或者找不到该下标的泛型，返回空
	 * </pre>
	 * 
	 * @param clazz
	 * @param index
	 * @return
	 */
	public static Class<?> getGenericType(Class<?> clazz, int index) {
		ParameterizedType genType = null;

		Type loopType = clazz.getGenericSuperclass();

		while (loopType != null) {
			if (loopType instanceof ParameterizedType) {
				// 如果当前类有是泛型就进入下一步
				genType = (ParameterizedType) loopType;
				break;
			}

			if (genType == null) {
				// 如果当前类没有泛型，就查找父类
				if (loopType instanceof Class) {
					// 如果当前类不是泛型，就找父类
					Class<?> c1 = (Class<?>) loopType;
					loopType = c1.getGenericSuperclass();
				} else {
					// 其实不会走到这步，防止死循环而已
					break;
				}
			}
		}

		// 搜索泛型时，只管找到的第一个有泛型定义的类
		if (genType != null && genType instanceof ParameterizedType) {
			// 必须有定义泛型
			Type[] params = genType.getActualTypeArguments();
			if (index < params.length && index >= 0) {
				// 下标不能超过泛型的数量
				Type res = params[index];
				if (res instanceof Class<?>) {
					// 必须是类
					return (Class<?>) params[index];
				}
			}
		}
		return null;
	}

	public static boolean isList(Class<?> clazz) {
		return clazz.isAssignableFrom(java.util.List.class);
	}

	/**
	 * 如果方法返回类型是数组或者list, 返回里面
	 * 
	 * @param m
	 * @return
	 */
	public static Class<?> getMethodReturnComponentType(Method m) {
		// ((ParameterizedType)type).getActualTypeArguments()[0]
		Class<?> c = m.getReturnType();
		if (c.isArray()) {
			// 如果是数组类的，返回数组的组成类型
			return c.getComponentType();
		} else if (c.isAssignableFrom(java.util.List.class)) {
			// 如果是List类的，返回泛型的定义类型
			Type genType = m.getGenericReturnType();
			if (genType != null && genType instanceof ParameterizedType) {
				// 必须有定义泛型
				Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
				if (params.length > 0) {
					// 下标不能超过泛型的数量
					Type res = params[0];
					if (res instanceof Class<?>) {
						// 必须是类
						return (Class<?>) res;
					}
				}
			}
		}
		return null;

	}

	/**
	 * 判断一个对象是否是指定的class，如果是就进行类型转换
	 * 
	 * @param obj
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K> K checkClass(Object obj, Class<K> clazz) {
		if (obj == null) {
			return null;
		}

		if (obj.getClass() != clazz) {
			return null;
		}

		return (K) obj;
	}

	/**
	 * 获取一个类的指定注解，会分析该类的所有接口
	 * 
	 * @param target
	 * @param annotationClass
	 * @return
	 */
	public static <A extends Annotation> A getAnnotation(Class<?> target, Class<A> annotationClass) {
		A res = null;
		res = target.getAnnotation(annotationClass);
		if (res == null) {
			// 如果目标class本身没有这个注解

			Class<?>[] infClassAry = target.getInterfaces();
			if (infClassAry != null) {
				for (Class<?> infClass : infClassAry) {
					// 循环检查所有的接口是否有该注解
					res = infClass.getAnnotation(annotationClass);
					if (res != null) {
						break;
					}
				}
			}
		}

		if (res == null) {
			// 如果接口中没有，就从父类中找
			Class<?> superClass = target.getSuperclass();
			while (superClass != null) {
				res = superClass.getAnnotation(annotationClass);
				if (res != null) {
					break;
				} else {
					superClass = superClass.getSuperclass();
				}
			}
		}

		return res;
	}

	/**
	 * 从方法上找注解，如果方法上没有，则查看方法所在的类
	 * 
	 * @param method
	 * @param annotationClass
	 * @return
	 */
	public static <A extends Annotation> A getAnnotationFromMethodAndClass(Method method, Class<A> annotationClass) {
		A res = null;
		res = method.getAnnotation(annotationClass);

		if (res == null) {
			return getAnnotation(method.getDeclaringClass(), annotationClass);
		}
		return res;
	}

	/**
	 * 寻找po中的getId方法
	 * 
	 * @param clazz
	 *            po的类
	 * @return
	 */
	public static Method getIdMethod(Class<?> clazz) {
		Method method = null;

		// 先从属性中找
		Field[] fields = clazz.getDeclaredFields();
		Field idField = null;
		for (Field f : fields) {
			if (f.isAnnotationPresent(javax.persistence.EmbeddedId.class)) {
				idField = f;
				break;
			}

			if (f.isAnnotationPresent(javax.persistence.Id.class)) {
				idField = f;
				break;
			}
		}

		if (idField != null) {
			log.debug("找到 id 字段:{}", idField.getName());

			String name = "get" + StringUtils.capitalize(idField.getName());

			try {
				method = clazz.getMethod(name);
				log.debug("通过 field 找到了 getter方法 ：{}", name);
			} catch (Exception e) {
			}
		}

		if (method == null) {
			// 如果在属性中没有找到，就找getter
			Method[] methods = clazz.getDeclaredMethods();
			for (Method m : methods) {
				if (m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
					if (m.isAnnotationPresent(Id.class) || m.isAnnotationPresent(EmbeddedId.class)) {
						log.debug("通过 getter找到了方法:{}", m.getName());
						method = m;
						break;
					}
				}
			}
		}

		return method;
	}

	/**
	 * 根据类名，获取这个类文件的生成时间，或者是这个文件的所在jar文件的生成时间
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static Date getClassBuildTime(String className) throws IOException {

		Date date = null;

		if (!StringUtils.hasText(className)) {
			return null;
		}

		String path = className.replace('.', '/') + ".class";

		URL url = Thread.currentThread().getContextClassLoader().getResource(path);
		if (url != null) {
			// 不管是文件类型还是jar，都可以通过这个方法获取时间
			URLConnection uc = url.openConnection();

			if (log.isDebugEnabled()) {
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {
					date = new Date(uc.getLastModified());
					log.debug("{}存在文件系统中，文件的最后更改时间是{}", className, date);
				} else if ("jar".equals(protocol)) {
					JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
					String jarFileName = jar.getName();
					File file = new File(jarFileName);
					date = new Date(file.lastModified());
					log.debug("{}存在jar文件{}中，jar文件的最后更改时间是{}", className, jarFileName, date);
				} else {
					log.warn("获取{}的BuildTime失败，无法解析protocol:{}", path, protocol);
				}
			}
		} else {
			log.error("无法获得 {} 的资源", path);
		}
		return date;
	}
}
