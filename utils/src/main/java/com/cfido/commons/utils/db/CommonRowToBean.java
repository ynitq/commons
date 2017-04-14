package com.cfido.commons.utils.db;

import java.lang.reflect.Constructor;

import org.hibernate.Query;
import org.hibernate.type.Type;

import com.cfido.commons.utils.utils.LogUtil;

/**
 * <pre>
 * 通用的Hibernate查询结果转换成为bean的转换器。
 * 
 * 通过对比查询结果中每列的类型,在bean中查找构造函数，并用该构造函数生成bean的实例
 * </pre>
 * 
 * @author 梁韦江 2015年8月6日
 */
public class CommonRowToBean<T> implements IRowToBean<T> {
	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(CommonRowToBean.class);

	public static <V> CommonRowToBean<V> newInstance(Class<V> entityClass) {
		return new CommonRowToBean<V>(entityClass);
	}

	/** 从泛型中找到的的类型 */
	private final Class<T> entityClass;

	/** bean 对应的构造函数 */
	private Constructor<T> beanConstructor;

	private CommonRowToBean(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	private Constructor<T> getBeanConstructor(Query hibernateQuery) {
		if (this.beanConstructor == null) {

			Type[] returnTypes = hibernateQuery.getReturnTypes();
			Class<?>[] parameterTypes = new Class<?>[returnTypes.length];
			for (int i = 0; i < returnTypes.length; i++) {
				parameterTypes[i] = returnTypes[i].getReturnedClass();
			}

			try {
				this.beanConstructor = this.entityClass.getDeclaredConstructor(parameterTypes);
			} catch (Exception e) {
				// 可能找不到构造函数，如果找不到就抛错，不让程序继续下去
				StringBuffer sb = new StringBuffer();
				sb.append(String.format("在 %s 中，无法对应参数的构造函数，参数列表为:", this.entityClass.getName()));
				for (Class<?> c : parameterTypes) {
					sb.append(c.getSimpleName());
					sb.append(",");
				}

				LogUtil.traceWarn(log, sb.toString());
				throw new RuntimeException(sb.toString());
			}

		}
		return beanConstructor;
	}

	@Override
	public T rowToBean(Query hibernateQuery, Object objFromListRow) {
		// 获得bean的构造函数
		Constructor<T> constructor = this.getBeanConstructor(hibernateQuery);

		// 生成构造函数的参数列表
		Object[] initArgs;
		if (objFromListRow.getClass().isArray()) {
			initArgs = (Object[]) objFromListRow;
		} else {
			initArgs = new Object[] {
					objFromListRow
			};
		}

		try {
			// 通过调用构造函数生成实例
			return constructor.newInstance(initArgs);
		} catch (Exception e) {
			LogUtil.traceError(log, e);
			throw new RuntimeException("查询数据转换成为bean时，发生了错误", e);
		}
	}
}
