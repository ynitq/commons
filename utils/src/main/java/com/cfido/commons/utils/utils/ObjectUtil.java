package com.cfido.commons.utils.utils;

/**
 * <pre>
 * 对象操作的工具
 * </pre>
 * 
 * @author 梁韦江
 */
public class ObjectUtil {

	/** 两个对象是否不一样 */
	public static boolean isNotEqual(int o1, int o2) {
		return o1 != o2;
	}

	/** 两个对象是否不一样 */
	public static boolean isNotEqual(Object o1, Object o2) {
		if (o1 == null) {
			return o2 != null;
		} else {
			return !o1.equals(o2);
		}
	}

}
