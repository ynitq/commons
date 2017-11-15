package com.cfido.commons.utils.db;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.cfido.commons.annotation.form.ABuildWhereExclude;
import com.cfido.commons.annotation.form.ABuildWhereFieldName;
import com.cfido.commons.annotation.form.ABuildWhereOptStr;
import com.cfido.commons.utils.utils.DateUtil;
import com.cfido.commons.utils.utils.LogUtil;
import com.cfido.commons.utils.utils.MethodUtil;
import com.cfido.commons.utils.utils.MethodUtil.MethodInfoOfGetter;

/**
 * <pre>
 * 通过反射的办法生成where sql
 * </pre>
 * 
 * @see ABuildWhereExclude
 * @see ABuildWhereFieldName
 * @see ABuildWhereOptStr
 * @see ABuildWhereTimeField
 * 
 * @author 梁韦江 2015年7月18日
 */
public class WhereBuilder {

	/** 如果参数是整形，但值为-999时，查询条件为 "字段名 is null" */
	public final static int INT_VALUE_IS_NULL = -999;

	/** 生成的sql是否是 jpa的风格 */
	public static boolean JPA_STYLE = false;

	public final static String OPT_LIKE = "like";

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(WhereBuilder.class);

	/**
	 * 最常用的，只有一个参数
	 * 
	 * @param form
	 * @return
	 */
	public static WhereBuilder create(Object form) {
		WhereBuilder builder = new WhereBuilder(form, "1=1");
		return builder;
	}

	/**
	 * app server用的最多的，那边的where前缀是 deleted=true
	 * 
	 * @param form
	 * @param wherePrefix
	 * @return
	 */
	public static WhereBuilder create(Object form, String wherePrefix) {
		WhereBuilder builder = new WhereBuilder(form, wherePrefix);
		return builder;
	}

	/**
	 * 处理一下作为like的参数，主要是替换掉%，并且在最后增加%
	 * 
	 * @param param
	 * @return
	 */
	public static String processLikeParam(String param) {
		Assert.notNull(param, "参数不能为空");

		StringBuilder sb = new StringBuilder();
		for (char c : param.trim().toCharArray()) {
			if (c != '%') {
				// 排除 %
				sb.append(c);
			}
		}
		// 最后添加 %
		sb.append('%');

		// 需要避免原来没有值的情况
		if (sb.length() > 1) {
			return sb.toString();
		} else {
			return " ";
		}
	}

	/** 表单对象 */
	private final Object form;
	private int jpaStyleIndex = 1;

	private final List<Object> paramList = new LinkedList<>();

	private final StringBuffer where = new StringBuffer(100);

	/** where的开头，为了避免没有条件的情况，至少有一句 1=1 */
	private final String wherePrefix;

	/** 禁止从外部new */
	private WhereBuilder(Object form, String wherePrefix) {
		super();
		this.form = form;
		this.wherePrefix = wherePrefix;
		this.build();
	}

	/** 获取jpa风格的参数序号，并且+1 */
	public int getAndIncreaseJpaStyleIndex() {
		int old = this.jpaStyleIndex;
		this.jpaStyleIndex++;
		return old;
	}

	public Object[] getParams() {
		return this.paramList.toArray();
	}

	public List<Object> getParamsList() {
		return this.paramList;
	}

	public String getWhereSql() {
		return this.where.toString();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("where: ").append(this.where);
		sb.append("\nparams: ");
		for (Object obj : this.paramList) {
			sb.append(obj);
			sb.append(" , ");
		}
		return sb.toString();
	}

	/**
	 * where条件拼接
	 */
	private void build() {
		if (form == null) {
			return;
		}

		where.append(' ');
		if (StringUtils.hasText(this.wherePrefix)) {
			where.append(this.wherePrefix);
			where.append(' ');
		}

		// 构建时，只管getter
		List<MethodInfoOfGetter> getters = MethodUtil.findGetter(form.getClass());

		for (MethodInfoOfGetter info : getters) {
			Method m = info.getOriginMethod();
			if (!info.isOpenType()) {
				// 只处理内部类型
				continue;
			}
			if (info.getAnnotation(ABuildWhereExclude.class, false) != null) {
				// 排除 用 ABuildWhereExclude 标记的
				continue;
			}

			String fieldName = this.getFieldName(info); // sql中的字段
			String optStr = this.getOptStr(info); // 操作符
			Object value = this.getValue(info, optStr); // 值

			if (value != null) {

				where.append(" and ").append(fieldName);

				if (m.getReturnType() == Integer.class
						&& Integer.parseInt(value.toString()) == INT_VALUE_IS_NULL) {
					// 整形要特殊处理，可以条件为is null时
					where.append(" is null ");
				} else {
					// 如果条件不是 is null，就拼接sql
					where.append(" ").append(optStr).append(" ?");

					if (JPA_STYLE) {
						// jpa风格的sql
						where.append(jpaStyleIndex);
						jpaStyleIndex++;
					}

					// 将值加入参数列表
					this.paramList.add(value);
				}
			}
		}
	}

	/**
	 * 获得在sql中的名字
	 */
	private String getFieldName(MethodInfoOfGetter m) {
		// 默认是用属性名
		String name = m.getPropName();

		// 如果有注解，就用注解上的名字
		ABuildWhereFieldName ano = m.getAnnotation(ABuildWhereFieldName.class, false);
		if (ano != null && StringUtils.hasText(ano.name())) {
			name = ano.name().trim();
		}
		return name;
	}

	/**
	 * 获得操作符， 默认是 “=”
	 * 
	 * @param m
	 * @return
	 */
	private String getOptStr(MethodInfoOfGetter m) {
		ABuildWhereOptStr ano = m.getAnnotation(ABuildWhereOptStr.class, false);
		if (ano != null && StringUtils.hasText(ano.optStr())) {
			// 如果有注解，就用注解上的
			return ano.optStr().trim();
		} else {
			// 如果没有注解，就用 “=”
			return "=";
		}
	}

	/**
	 * 获得在参数中值
	 */
	private Object getValue(MethodInfoOfGetter info, String optStr) {
		Object value = null;
		try {
			// 执行方法，先获取值
			value = info.getOriginMethod().invoke(form);

			if (value != null) {
				// 如果有值，就需要额外处理

				if (info.getReturnTypeClass() == String.class) {
					// 字符串类型的特殊处理
					String old = (String) value;

					if (StringUtils.isEmpty(old)) {
						// 字符串没有内容，如果没有内容，也返回null
						return null;
					}

					// 如果是字符串行，要特别检测一下是否声明是 like的操作符
					if (OPT_LIKE.equals(optStr)) {


						// 将所有的"%"去掉，避免用户在输入的字符串中有%号时，可能导致的全表扫描问题
						String escapedValue = processLikeParam(old);

						boolean mustBeAllLike = false;
						ABuildWhereOptStr optAnno = info.getAnnotation(ABuildWhereOptStr.class, false);
						if (optAnno != null) {
							mustBeAllLike = optAnno.mustBeAllLike();
						}

						// 迫不得已要做全表索引的时候加这个参数--表设计的问题，我是有苦衷的☺
						if (mustBeAllLike) {
							value = "%" + escapedValue;
						} else {
							value = escapedValue;
						}
					}
				} else if (info.getReturnTypeClass() == Date.class) {
					// 日期型 也要特殊处理
					Date src = (Date) value;
					if (((String) value).length() > 10) {
						if (">=".equals(optStr) || ">".equals(optStr)) {
							// 如果是开始时间，需要设置为 00:00:00
							value = DateUtil.parserTimestapForDate(src, true);
						} else if ("<=".equals(optStr) || "<".equals(optStr)) {
							// 如果是结束时间，需要设置为 23:59:59
							value = DateUtil.parserTimestapForDate(src, false);
						}
					} else {
						value = src;
					}

				}
			}
		} catch (Exception e) {
			LogUtil.traceError(log, e);
		}
		return value;
	}

}
