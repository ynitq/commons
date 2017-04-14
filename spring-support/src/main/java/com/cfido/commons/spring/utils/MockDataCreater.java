package com.cfido.commons.spring.utils;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.cfido.commons.annotation.api.AMock;
import com.cfido.commons.utils.utils.ClassUtil;
import com.cfido.commons.utils.utils.OpenTypeUtil;
import com.cfido.commons.utils.utils.StringUtils;

/**
 * <pre>
 * 用于生成模拟数据的工具，
 * 是用代理的方法拦截getter，并生成模拟数据，不会实际执行getter
 * </pre>
 * 
 * @author 梁韦江 2016年6月30日
 */
public class MockDataCreater {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MockDataCreater.class);

	/**
	 * 如果出现递归生成数据时，最多生成3层
	 */
	public static int DEEP_LIMIT = 5;

	private final Class<?> clazz;
	private final int deep;
	private final int id;
	private final Enhancer enhancer = new Enhancer();

	/** 动态代理的拦截器 */
	private final MethodInterceptor proxyCallback = new MethodInterceptor() {
		@Override
		public Object intercept(Object paramObject, Method paramMethod, Object[] paramArrayOfObject, MethodProxy paramMethodProxy)
				throws Throwable {
			return MockDataCreater.this.doIntercept(paramObject, paramMethod, paramArrayOfObject, paramMethodProxy);
		}
	};

	private MockDataCreater(Class<?> clazz) {
		this(clazz, 0, 1);
	}

	private MockDataCreater(Class<?> clazz, int deep, int id) {
		super();
		this.clazz = clazz;
		this.deep = deep;
		this.id = id;

		// 设置要创建动态代理的类
		enhancer.setSuperclass(this.clazz);

		// 设置回调，这里相当于是对于代理类上所有方法的调用，都会调用CallBack，而Callback则需要实行intercept()方法进行拦截
		enhancer.setCallback(this.proxyCallback);
	}

	/**
	 * 获得模拟数据
	 * 
	 * @return
	 */
	private Object newInstance() {
		if (this.deep > DEEP_LIMIT) {
			// 递归层数不能太多
			return null;
		}

		try {
			clazz.getConstructor();

			return this.enhancer.create();
		} catch (Exception e) {
			log.warn("类型 {} 没有无参数的构造方法，无法生成模拟数据", clazz.getName());
			return null;
		}

	}

	private Object getMockDateForNotList(Class<?> returnCalss, String propName, Method paramMethod) {
		Object propValue = null;
		if (OpenTypeUtil.isOpenType(returnCalss)) {
			// 如果是可以直接转的类型，就直接转;
			String value = getDefaultSrtingValueForSetter(propName, returnCalss, paramMethod, this.id);// 默认值
			propValue = OpenTypeUtil.parserFromString(value, returnCalss, "-99999");
		} else {
			MockDataCreater creater = new MockDataCreater(returnCalss, deep + 1, 1);
			propValue = creater.newInstance();
		}
		return propValue;
	}

	/**
	 * 执行拦截过程，根据getter返回的类型生成数据
	 * 
	 * @param paramObject
	 * @param paramMethod
	 * @param paramArrayOfObject
	 * @param paramMethodProxy
	 * @return
	 * @throws Throwable
	 */
	private Object doIntercept(Object paramObject, Method paramMethod, Object[] paramArrayOfObject, MethodProxy paramMethodProxy)
			throws Throwable {

		Object propValue = null;

		try {

			String propName = getPropName(paramMethod);

			if (propName != null) {
				// 只处理 getter：get或者is开头，并且是没有产生的
				Class<?> returnCalss = paramMethod.getReturnType();

				if (returnCalss.equals(Object.class)) {
					// TODO 在生成模拟数据时，如果返回的类型是泛型，会出错，有时间再处理
					return null;
				}

				if (ClassUtil.isList(returnCalss)) {
					if (this.deep < DEEP_LIMIT) {
						// 如果是list
						// 如果需要返回一个list
						List<Object> list = new LinkedList<>();
						propValue = list;

						// 获得list模拟数据的数量
						int size = 2;
						AMock amock = paramMethod.getAnnotation(AMock.class);
						if (amock != null) {
							size = amock.size();
						}

						// 获取list中的泛型类型
						Class<?> componentClazz = ClassUtil.getMethodReturnComponentType(paramMethod);

						for (int i = 0; i < size; i++) {
							list.add(this.getMockDateForNotList(componentClazz, componentClazz.getSimpleName(), paramMethod));
						}
					}
				} else {
					propValue = this.getMockDateForNotList(returnCalss, propName, paramMethod);
				}
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

		return propValue;
	}

	private String getPropName(Method paramMethod) {
		if (paramMethod.getParameterTypes().length != 0) {
			// getter不能有参数
			return null;
		}

		if (paramMethod.getReturnType() == null) {
			// getter必须有返回值
			return null;
		}

		String methodName = paramMethod.getName();
		String propName = null;

		if (methodName.startsWith("get")) {
			// 如果是get开头
			propName = methodName.substring(3);
		} else if (methodName.startsWith("is")) {
			// 如果是is开头
			propName = methodName.substring(2);
		}

		if (propName != null) {
			// 如果是getter，则返回
			return StringUtils.lowerFirstChar(propName);
		} else {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz) {
		if (clazz == null) {
			return null;
		}

		MockDataCreater creater = new MockDataCreater(clazz);
		return (T) creater.newInstance();
	}

	/**
	 * 获得setter参数的默认值
	 * 
	 * @param propName
	 * @param m
	 * @return
	 */
	private static String getDefaultSrtingValueForSetter(String propName, Class<?> paramClass, Method m, int id) {

		String value = null;// 默认值
		AMock mock = m.getAnnotation(AMock.class);
		boolean isId = false;
		if (mock != null) {
			// 如果有注解，就用注解的设置
			value = mock.value();
			isId = mock.id();
		}

		if (!isId) {
			isId = "id".equals(propName);
		}

		if (StringUtils.isEmpty(value)) {
			// 如果为空，则用方法名作为值
			if (isId) {
				// 如果该setter是id
				if (paramClass == String.class) {
					// 如果是String型的，值为：xxx_1
					value = propName + "_" + id;
				} else {
					// 如果不是String型的，值就是 id
					value = String.valueOf(id);
				}
			} else {
				value = propName;
			}
		}

		return value;

	}

}
