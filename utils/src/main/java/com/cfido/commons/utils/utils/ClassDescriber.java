package com.cfido.commons.utils.utils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.cfido.commons.utils.utils.ClassUtil.MethodInfo;

/**
 * <pre>
 * Class的说明生成器
 * </pre>
 * 
 * @author 梁韦江 2017年5月9日
 */
public class ClassDescriber {
	/**
	 * <pre>
	 * 输出类说明时的字段过滤器
	 * </pre>
	 * 
	 * @author 梁韦江 2017年5月9日
	 * 
	 */
	public static interface IFieldDescFilter {
		/** 该方法是否是合法的 */
		boolean isValidMethod(Method method);

		/** 返回类型是否是合法的 */
		boolean isValidReturnType(Class<?> clazz);
	}

	/**
	 * <pre>
	 * 默认的过滤器，指定包开头的类才需要显示类结构
	 * </pre>
	 * 
	 * @author 梁韦江 2017年5月9日
	 * 
	 */
	private class DefaultFieldDescFilter implements IFieldDescFilter {

		private final List<String> packageList = new LinkedList<>();

		public DefaultFieldDescFilter() {
			this.packageList.add("com.cfido");
			this.packageList.add("com.linzi");
		}

		@Override
		public boolean isValidMethod(Method method) {
			return true;
		}

		@Override
		public boolean isValidReturnType(Class<?> clazz) {
			String packagename = clazz.getPackage().getName();
			for (String string : packageList) {
				if (packagename.startsWith(string)) {
					return true;
				}
			}
			return false;
		}

	}

	/**
	 * 生成类的描述
	 * 
	 * @param clazz
	 * @return
	 */
	public static String create(Class<?> clazz) {
		return create(clazz, null);
	}

	/**
	 * 生成类的描述
	 * 
	 * @param clazz
	 * @return
	 */
	public static String create(Class<?> clazz, IFieldDescFilter filter) {
		ClassDescriber cd = new ClassDescriber(filter);
		cd.parser(clazz, "");
		String str = cd.buff.toString();
		return str;
	}

	private final StringBuffer buff = new StringBuffer();

	/** 默认过滤器 */
	private final IFieldDescFilter defaultFilter = new DefaultFieldDescFilter();

	private final IFieldDescFilter filter;

	/** 保存已经分析过的类型 */
	private final Set<Class<?>> parseredSet = new HashSet<>();

	private ClassDescriber() {
		this(null);
	}

	private ClassDescriber(IFieldDescFilter filter) {
		super();
		this.filter = filter == null ? this.defaultFilter : filter;
	}

	/**
	 * 分析类
	 * 
	 * @return
	 */
	private void parser(Class<?> respClass, String prefix) {

		if (this.parseredSet.contains(respClass)) {
			// 如果已经分析过，就不在分析了，免得无需递归
			return;
		}
		this.parseredSet.add(respClass);

		List<MethodInfo> list = ClassUtil.findGetter(respClass);

		for (MethodInfo res : list) {
			if (!this.filter.isValidMethod(res.getOriginMethod())) {
				// 过滤一下方法
				continue;
			}

			if (res != null) {
				buff.append(prefix);

				// code:Integer
				buff.append(res.getName());
				buff.append(":");
				buff.append(res.getReturnTypeClass().getSimpleName());

				// 如果是数组，后面加 []， 例如: code:Integer[]
				if (res.isArray()) {
					buff.append("[]");
				}

				// 备注
				if (StringUtils.hasText(res.getMemo())) {
					buff.append(" (");
					buff.append(res.getMemo());
					buff.append(")");
				}

				buff.append('\n');

				if (!res.isInnerReturnType()
						&& this.filter.isValidReturnType(res.getReturnTypeClass())
						&& !this.parseredSet.contains(res.getReturnTypeClass())) {
					// 如果返回的类型不是内部类型，并且需要显示类结构说明
					if (res.isArray()) {
						// 如果是数组，就加多一行
						buff.append(prefix);
						buff.append('\t');
						buff.append(String.format("[%s的结构]\n", res.getReturnTypeClass().getSimpleName()));
					}
					parser(res.getReturnTypeClass(), prefix + "\t");
				}
			}
		}
	}

}
