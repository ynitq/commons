package com.cfido.commons.spring.apiServer.core;

import com.cfido.commons.annotation.api.AClass;
import com.cfido.commons.annotation.bean.AComment;
import com.cfido.commons.utils.utils.StringUtils;

/**
 * <pre>
 * 放置搜索接口的结果
 * </pre>
 * 
 * @author 梁韦江 2016年6月30日
 */
public class FindInferfaceResult {
	private final AClass aclass;
	private final Class<?> infClass;// 一个实现类是可以实现多个接口的

	private String prefix;

	public FindInferfaceResult(AClass aclass, Class<?> infClass) {
		super();
		this.aclass = aclass;
		this.infClass = infClass;
	}

	/**
	 * 获得接口类的备注
	 */
	public String getInfMemo() {
		AComment ann = this.infClass.getAnnotation(AComment.class);
		if (ann != null) {
			return ann.comment();
		}
		return null;
	}

	/**
	 * 接口的class
	 * 
	 * @return
	 */
	public Class<?> getInfClass() {
		return infClass;
	}

	/**
	 * 获得这个接口的key。
	 * 
	 * <pre>
	 * 先看注解中是否有，如果没有就用类名，要去掉前面的I
	 * </pre>
	 * 
	 * @return
	 */
	public String getInfKey() {
		if (this.prefix == null) {
			prefix = this.aclass.value();
			if (StringUtils.isEmpty(prefix)) {
				// 如果注解中没有什么前缀，就用类名
				String classname = this.infClass.getSimpleName();
				if (classname.startsWith("I") && classname.length() > 1) {
					// 如果是I开头，就将i去掉
					prefix = classname.substring(1);
				}
			}

		}
		return prefix;
	}
}
