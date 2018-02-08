package com.cfido.commons.utils.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.utils.utils.MethodUtil.MethodInfoOfGetter;

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
	private static class DefaultFieldDescFilter implements IFieldDescFilter {

		private final Set<String> packageList = new HashSet<>();

		public DefaultFieldDescFilter() {
			this.packageList.add("com.cfido");
			this.packageList.add("com.linzi");
			this.packageList.add("haimian");
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

	/** 为默认的过滤器增加包名 */
	public static void addPackageToDefaultFilter(String packageName) {
		if (StringUtils.hasText(packageName)) {
			DEFAULT_FILTER.packageList.add(packageName);
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
		cd.parser(clazz, cd.rootMap);
		String jsonStr = JSON.toJSONString(cd.rootMap, SerializerFeature.PrettyFormat,
				SerializerFeature.UseSingleQuotes, SerializerFeature.MapSortField);

		return jsonStr.replace("\t", "    ");
	}

	private final Map<String, Object> rootMap = new HashMap<>();

	/** 默认过滤器 */
	private static final DefaultFieldDescFilter DEFAULT_FILTER = new DefaultFieldDescFilter();

	private final IFieldDescFilter filter;

	/** 保存已经分析过的类型 */
	private final Set<Class<?>> parseredSet = new HashSet<>();

	private ClassDescriber(IFieldDescFilter filter) {
		super();
		this.filter = filter == null ? DEFAULT_FILTER : filter;
	}

	/**
	 * 分析类
	 * 
	 * @return
	 */
	private void parser(Class<?> respClass, Map<String, Object> nodeMap) {

		if (this.parseredSet.contains(respClass)) {
			// 如果已经分析过，就不在分析了，免得无限递归
			return;
		}
		this.parseredSet.add(respClass);

		List<MethodInfoOfGetter> list = MethodUtil.findGetter(respClass);

		for (MethodInfoOfGetter res : list) {

			if (!this.filter.isValidMethod(res.getOriginMethod())) {
				// 过滤一下方法
				continue;
			}

			String name = res.getPropName() + ":" + res.getReturnTypeClass().getSimpleName();
			// 如果是数组，后面加 []， 例如: code[]:Integer
			if (res.isArray()) {
				name += "[]";
			}

			// 备注
			String memo = "";
			AComment memoAnno = res.getAnnotation(AComment.class, false);
			if (memoAnno != null && StringUtils.hasText(memoAnno.value())) {
				memo = memoAnno.value();
			}

			if (res.isOpenType()) {
				// 如果返回类型是普通类型
				nodeMap.put(name, memo);
			} else {
				if (this.parseredSet.contains(res.getReturnTypeClass())
						|| !this.filter.isValidReturnType(res.getReturnTypeClass())) {
					// 如果这个类已经存在了或者不需要描述
					nodeMap.put(name, memo);
				} else {
					// 否则就需要显示类结构说明
					Map<String, Object> subMap = new HashMap<>();
					if (StringUtils.hasText(memo)) {
						// 属性的备注
						subMap.put(" //", memo);
					}
					nodeMap.put(name, subMap);

					this.parser(res.getReturnTypeClass(), subMap);
				}
			}
		}
	}

}
