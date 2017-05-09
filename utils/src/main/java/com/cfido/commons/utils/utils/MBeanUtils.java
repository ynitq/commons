package com.cfido.commons.utils.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <pre>
 * 为mbean提供的工具集合
 * </pre>
 * 
 * @author 梁韦江 2016年10月12日
 */
public class MBeanUtils {

	/**
	 * <pre>
	 * 在执行  objectToMap 时，是否忽略该方法
	 * </pre>
	 * 
	 * @author 梁韦江 2016年10月12日
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@Documented
	public static @interface IgnoreWhenObjectToMap {
		/** 备注 */
		String value() default "";
	}

	/**
	 * 将对象的内容输出到map
	 * 
	 * @param obj
	 *            要输出的对象
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> objectToMap(Object obj) throws Exception {

		Assert.notNull(obj, "要转换的对象不能为空");

		Map<String, Object> map = new HashMap<>();

		BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor property : propertyDescriptors) {
			String key = property.getName();
			if ("class".equals(key)) {
				// 过滤getClass()
				continue;
			}

			Method getter = property.getReadMethod();
			if (getter != null) {
				Object value;

				IgnoreWhenObjectToMap anno = getter.getAnnotation(IgnoreWhenObjectToMap.class);
				if (anno != null) {
					if (StringUtils.hasText(anno.value())) {
						// 如果注解上有备注就用备注
						value = anno.value();
					} else {
						value = String.format("%s属性设置了忽略", key);
					}
				} else {
					value = getter.invoke(obj);
				}
				map.put(key, value);
				// System.out.printf("%s.%s\n", obj.getClass().getName(),
				// getter.getName());
				// System.out.println();
			}
		}

		return map;
	}
}
