package com.cfido.commons.utils.utils;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <pre>
 * 在Class搜索Method的工具
 * </pre>
 * 
 * @author 梁韦江 2015年5月15日
 */
public class MethodUtil {

	/** 对Getter方法的包装 */
	public class MethodInfoOfGetter extends BaseMethodInfo {
		private boolean array;// 是否数组或者list
		private final boolean openType; // 返回的类型是否是常见内部类型
		private Class<?> returnTypeClass; // 返回的类型

		public MethodInfoOfGetter(String propName, Method method) {
			super(propName, method);

			// 判断返回的对象是否集合类型
			Class<?> compClass = ClassUtil.getMethodReturnComponentType(method);
			if (compClass != null) {
				// 如果是数组或者list，这用数组结构的类型作为返回类型
				this.array = true;
				this.returnTypeClass = compClass;
			} else {
				this.array = false;
				this.returnTypeClass = method.getReturnType();
			}

			// 返回类型是否是内部类型
			this.openType = OpenTypeUtil.isOpenType(this.returnTypeClass);
		}

		/** getter的返回类型 */
		public Class<?> getReturnTypeClass() {
			return returnTypeClass;
		}

		/** 返回类型是否是array */
		public boolean isArray() {
			return array;
		}

		/** 返回类型是否是 常见类型 */
		public boolean isOpenType() {
			return openType;
		}

	}

	/** 对Setter方法的包装 */
	public class MethodInfoOfSetter extends BaseMethodInfo {

		/** setter的参数类型 */
		private final Class<?> paramClass;

		public MethodInfoOfSetter(String propName, Method method) {
			super(propName, method);
			this.paramClass = method.getParameterTypes()[0];
		}

		/** setter的参数类型 */
		public Class<?> getParamClass() {
			return paramClass;
		}
	}

	/** 遍历一个类所有方法时，用的过滤器 */
	private interface IMethodFilter<T extends BaseMethodInfo> {
		/** 根据方法获取属性名 */
		String getPropName(Method method);

		boolean isMatch(Method method);

		T newMethodInfo(Method method);
	}

	/** 对一个method包装的基类 */
	protected abstract class BaseMethodInfo {

		protected final Method method; // 原始的Method
		protected final String propName;// 字段名

		private BaseMethodInfo(String propName, Method method) {
			Assert.notNull(propName, "字段名不能为空");
			Assert.notNull(method, "方法能为空");

			this.propName = propName;
			this.method = method;
		}

		/** 是否有指定的注解 */
		public boolean isAnnotationPresent(Class<? extends Annotation> annoClass, boolean findInDeclaringClass) {
			return this.getAnnotation(annoClass, findInDeclaringClass) != null;
		}

		/** 寻找注解 */
		public <A extends Annotation> A getAnnotation(Class<A> annoClass, boolean findInDeclaringClass) {

			A anno = null;

			// 获取这个注解的目标
			Target targetAnno = annoClass.getAnnotation(Target.class);

			if (isTarget(targetAnno, ElementType.METHOD)) {
				// 先在方法上找
				anno = this.method.getAnnotation(annoClass);
			}

			if (anno == null && isTarget(targetAnno, ElementType.FIELD)) {
				// 如果方法上没有，就在字段中找
				Map<String, A> annoInFieldMap = ClassUtil.getAllAnnoFromField(this.method.getDeclaringClass(),
						annoClass);
				anno = annoInFieldMap.get(this.propName);
			}

			if (findInDeclaringClass && anno == null && isTarget(targetAnno, ElementType.TYPE)) {
				// 如果字段上也没找到，就查一下方法所在的类
				anno = ClassUtil.getAnnotation(this.method.getDeclaringClass(), annoClass);
			}

			return anno;
		}

		/** 寻找注解，默认是不在所属类上找 */
		public <A extends Annotation> A getAnnotation(Class<A> annoClass) {
			return this.getAnnotation(annoClass, false);
		}

		/**
		 * 原始的Method
		 */
		public Method getOriginMethod() {
			return method;
		}

		/** 首字母小写的字段名 */
		public String getPropName() {
			return propName;
		}

	}

	/** 用于存储一个类所有在字段上的注解 */
	private final static MethodUtil instance = new MethodUtil();

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MethodUtil.class);

	/** 查看一个注解是否可以用于某个类型 */
	private static boolean isTarget(Target targetAnno, ElementType type) {
		if (targetAnno == null) {
			// 如果没有这个Target，表示可以用于任何地方
			return true;
		}

		ElementType[] all = targetAnno.value();
		if (all == null || all.length == 0) {
			// 根据Target的说明，如果没有设置指定的类型，就表示可以用于任何类型
			return true;
		}

		for (ElementType t : all) {
			if (t == type) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 在一个类中，寻找所有的getter
	 */
	public static List<MethodInfoOfGetter> findGetter(Class<?> clazz) {
		return findMethod(clazz, instance.getterFilter);
	}

	/**
	 * 寻找po中的getId方法
	 * 
	 * @param clazz
	 *            po的类
	 * @return
	 */
	public static Method getIdMethod(Class<?> clazz) {

		List<MethodInfoOfGetter> list = findGetter(clazz);

		MethodInfoOfGetter found = null;
		for (MethodInfoOfGetter getter : list) {
			if (getter.getAnnotation(javax.persistence.Id.class) != null) {
				found = getter;
				break;
			}
			if (getter.getAnnotation(javax.persistence.EmbeddedId.class) != null) {
				found = getter;
				break;
			}
		}

		if (found != null) {
			return found.getOriginMethod();
		} else {
			return null;
		}

	}

	/**
	 * 在一个类中，寻找所有的setter
	 */
	public static List<MethodInfoOfSetter> findSetter(Class<?> clazz) {
		return findMethod(clazz, instance.setterFilter);
	}

	/**
	 * 在一个类中，根据过滤器寻找所有的Method
	 */
	private static <T extends BaseMethodInfo> List<T> findMethod(Class<?> clazz, IMethodFilter<T> filter) {
		Assert.notNull(clazz, "目标类型不能为空");
		Assert.notNull(filter, "过滤器不能为空");

		List<T> list = new LinkedList<>();

		for (Method method : clazz.getMethods()) {
			if (filter.isMatch(method)) {
				String propName = filter.getPropName(method);
				if (StringUtils.hasText(propName)) {
					T info = filter.newMethodInfo(method);
					list.add(info);
				} else {
					log.warn("出现了奇怪现象，无法从方法名 {} 中 分析出属性名", method.getName());
				}
			}
		}

		return list;
	}

	/** 搜索 getter 时用的过滤器 */
	private final IMethodFilter<MethodInfoOfGetter> getterFilter = new IMethodFilter<MethodInfoOfGetter>() {

		@Override
		public String getPropName(Method method) {

			String name = method.getName();
			String attrName = null;

			// 分析方法名
			if (name.startsWith("is")) {
				// 可以是is开头
				attrName = name.substring(2);
			} else if (name.startsWith("get")) {
				// 可以是 get开头
				attrName = name.substring(3);
			} else {
				// 如果都不是
				return null;
			}

			return StringUtils.uncapitalize(attrName);
		}

		@Override
		public boolean isMatch(Method method) {
			String name = method.getName(); // 名字
			Class<?> returnType = method.getReturnType(); // 返回类型
			Class<?>[] paramTypes = method.getParameterTypes(); // 参数

			if (paramTypes != null && paramTypes.length > 0) {
				// 不能有参数
				return false;
			}
			if (method.getDeclaringClass() == Object.class) {
				// 不处理 Object基类中的方法
				return false;
			}

			if (returnType == void.class) {
				// 必须有返回类型
				return false;
			}

			// 分析方法名
			if (name.startsWith("is")) {
				// 可以是is开头
				if (returnType != boolean.class && returnType != Boolean.class) {
					// is开头的，返回类型必须是boolean类型
					return false;
				}
				if (name.length() == 2) {
					// 不能就叫is()
					return false;
				}
			} else if (name.startsWith("get")) {
				if (name.length() == 3) {
					// 不能就叫 get()
					return false;
				}
			} else {
				// 如果不是 is或者get开通，就肯定不是getter
				return false;
			}

			return true;
		}

		@Override
		public MethodInfoOfGetter newMethodInfo(Method method) {
			return new MethodInfoOfGetter(this.getPropName(method), method);
		}

	};

	/** 搜索 setter 时用的过滤器 */
	private final IMethodFilter<MethodInfoOfSetter> setterFilter = new IMethodFilter<MethodInfoOfSetter>() {

		@Override
		public String getPropName(Method method) {
			String name = method.getName(); // 名字
			return StringUtils.uncapitalize(name.substring(3));
		}

		@Override
		public boolean isMatch(Method method) {
			String name = method.getName(); // 名字
			Class<?> returnType = method.getReturnType(); // 返回类型
			Class<?>[] paramTypes = method.getParameterTypes(); // 参数

			if (!name.startsWith("set") || name.length() <= 3) {
				// 必须是setXXX的方法
				return false;
			}

			if (paramTypes == null || paramTypes.length != 1) {
				// 有且只有一个参数
				return false;
			}

			if (returnType != void.class) {
				// setter 不能有返回类型
				return false;
			}
			return true;
		}

		@Override
		public MethodInfoOfSetter newMethodInfo(Method method) {
			return new MethodInfoOfSetter(this.getPropName(method), method);
		}

	};

	/** 不允许new这个类 */
	private MethodUtil() {
	}

}
