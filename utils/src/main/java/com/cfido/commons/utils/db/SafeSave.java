package com.cfido.commons.utils.db;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import org.springframework.util.Assert;

import com.cfido.commons.utils.utils.LogUtil;
import com.cfido.commons.utils.utils.MethodUtil;
import com.cfido.commons.utils.utils.MethodUtil.MethodInfoOfGetter;
import com.cfido.commons.utils.utils.MethodUtil.MethodInfoOfSetter;

/**
 * <pre>
 * 安全保存的工具，用于防止过长的内容
 * </pre>
 * 
 * @author 梁韦江 2017年7月5日
 */
public class SafeSave<PO> {
	/**
	 * <pre>
	 * 用于存储表中和各个字段的长度，防止在保存的时候数据的长度过长
	 * </pre>
	 * 
	 * @author 梁韦江 2015年7月18日
	 */
	public class StringPropWithLength {
		private final Method getter;
		private final int length;
		private final Method setter;

		private StringPropWithLength(Method getter, int length, Method setter) {
			super();
			this.getter = getter;
			this.length = length;
			this.setter = setter;
		}

		@Override
		public String toString() {
			return String.format("setter:%s getter:%s 长度:%d", this.setter.getName(), this.getter.getName(), this.length);
		}
	}

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SafeSave.class);

	private List<StringPropWithLength> getterList;

	private final Class<PO> entityClass;

	public SafeSave(Class<PO> entityClass) {
		Assert.notNull(entityClass, "类型不能为空");

		this.entityClass = entityClass;
	}

	private void checkInit() {
		if (this.getterList == null) {
			synchronized (this) {
				if (this.getterList == null) {
					this.getterList = this.findStringPropWithLength();
				}
			}
		}
	}

	/** 处理 PO的字符串内容 */
	public void process(PO po) {

		this.checkInit();

		for (StringPropWithLength en : this.getterList) {
			try {
				String value = (String) en.getter.invoke(po);
				if (value != null && value.length() > en.length) {
					String newvalue = com.cfido.commons.utils.utils.StringUtilsEx.substring(value, 0, en.length);
					en.setter.invoke(po, newvalue);

					log.warn("存储对象 {} 时，字段 {} 的值的长度过长，自动将长度截取到 {}, 新的值为:{}",
							this.entityClass.getSimpleName(),
							en.getter.getName().substring(3).toLowerCase(),
							en.length,
							newvalue);
				}
			} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
				LogUtil.traceError(log, e);
			}
		}

	}

	/**
	 * 找出所有Column注解定义了长度的字符串属性，用于放在保存到数据库时，内容长度过长
	 * 
	 * @param entityClass
	 * @return
	 */
	private List<StringPropWithLength> findStringPropWithLength() {
		List<StringPropWithLength> list = new LinkedList<>();

		// 找出所有的setter
		List<MethodInfoOfSetter> setterList = MethodUtil.findSetter(entityClass);

		// 将所有setter放到map中
		Map<String, Method> setterMap = new HashMap<>();
		for (MethodInfoOfSetter setter : setterList) {
			setterMap.put(setter.getPropName(), setter.getOriginMethod());
		}

		// 找出所有的getter
		List<MethodInfoOfGetter> getterList = MethodUtil.findGetter(entityClass);
		for (MethodInfoOfGetter getter : getterList) {
			if (getter.getReturnTypeClass() == String.class) {
				// 必须是String 类型的
				Column anno = getter.getAnnotation(Column.class);
				if (anno != null && anno.length() > 0) {
					// 必须有长度限制
					Method setter = setterMap.get(getter.getPropName());
					if (setter != null) {

						StringPropWithLength en = new StringPropWithLength(getter.getOriginMethod(), anno.length(), setter);

						// 如果还能找到对应的setter，就添加带list中
						list.add(en);

						log.debug("发现需要检查长度的字段: {}", en);
					}
				}
			}

		}
		return list;
	}

}
