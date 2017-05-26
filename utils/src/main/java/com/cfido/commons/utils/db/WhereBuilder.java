package com.cfido.commons.utils.db;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.cfido.commons.annotation.form.ABuildWhereExclude;
import com.cfido.commons.annotation.form.ABuildWhereFieldName;
import com.cfido.commons.annotation.form.ABuildWhereOptStr;
import com.cfido.commons.annotation.form.ABuildWhereTimeField;
import com.cfido.commons.utils.utils.DateUtil;
import com.cfido.commons.utils.utils.LogUtil;

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

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(WhereBuilder.class);

	/** 如果参数是整形，但值为-999时，查询条件为 "字段名 is null" */
	public final static int INT_VALUE_IS_NULL = -999;

	public final static String OPT_LIKE = "like";

	/** 生成的sql是否是 jpa的风格 */
	public static boolean JPA_STYLE = false;

	private int jpaStyleIndex = 1;

	private static java.util.Set<Class<?>> classSet = new HashSet<>();
	static {
		classSet.add(String.class);
		classSet.add(Integer.class);
		classSet.add(Long.class);
		classSet.add(Float.class);
		classSet.add(Double.class);
		classSet.add(Boolean.class);
		classSet.add(Date.class);
	}

	private final String timeField;
	private final String asName;
	private final boolean isSetAsName;
	private final String wherePrefix;// where的开头，为了避免没有条件的情况，至少有一句 1=1

	private final Object form;
	private final StringBuffer where = new StringBuffer(100);
	private final List<Object> paramList = new LinkedList<>();

	private WhereBuilder(Object form, String timeField, String asName, String wherePrefix) {
		super();
		this.timeField = timeField;
		this.asName = asName;
		this.isSetAsName = StringUtils.hasText(asName);
		this.form = form;
		this.wherePrefix = wherePrefix;
		this.build();
	}

	/**
	 * 省略 wherePreFix , 兼容原来的代码
	 * 
	 * @param form
	 * @param timeField
	 * @param asName
	 * @return
	 */
	public static WhereBuilder create(Object form, String timeField, String asName) {
		WhereBuilder builder = new WhereBuilder(form, timeField, asName, "1=1");
		return builder;
	}

	/**
	 * 全参数的版本
	 * 
	 * @param form
	 * @param timeField
	 * @param asName
	 * @param wherePrefix
	 * @return
	 */
	public static WhereBuilder create(Object form, String timeField, String asName, String wherePrefix) {
		WhereBuilder builder = new WhereBuilder(form, timeField, asName, wherePrefix);
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
		WhereBuilder builder = new WhereBuilder(form, null, null, wherePrefix);
		return builder;
	}

	/**
	 * 最常用的，只有一个参数
	 * 
	 * @param form
	 * @return
	 */
	public static WhereBuilder create(Object form) {
		WhereBuilder builder = new WhereBuilder(form, null, null, "1=1");
		return builder;
	}

	/**
	 * 判断getter是否需要包含在sql中
	 * 
	 * @param m
	 *            getter方法
	 * @return
	 */
	private boolean isIncludeMethod(Method m) {
		if (!(m.getName().startsWith("get") || m.getName().startsWith("is")) || m.getName().length() <= 3) {
			return false;
		}

		if (m.getAnnotation(ABuildWhereExclude.class) != null) {
			return false;
		}

		// 返回的类型是合法的类型
		Class<?> c = m.getReturnType();
		return c.isPrimitive() || classSet.contains(c);
	}

	/**
	 * 获得在sql中的名字
	 * 
	 * @param m
	 * @return
	 */
	private String getFieldName(Method m) {
		if (m.isAnnotationPresent(ABuildWhereTimeField.class)) {
			// 如果声明了时时间类型，需要特别处理一下
			ABuildWhereTimeField ano = m.getAnnotation(ABuildWhereTimeField.class);
			if (ano != null && StringUtils.hasText(ano.filed())) {
				// 最高优先级为注解里定义的字段名
				return ano.filed();
			}
		}

		if (m.getReturnType().equals(Date.class)) {
			ABuildWhereTimeField ano = m.getAnnotation(ABuildWhereTimeField.class);
			if (ano != null && StringUtils.hasText(timeField)) {
				// 外面设置的时间字段作为字段名的优先级最高--降一级(●'◡'●)
				return this.timeField;
			}
			if (StringUtils.isEmpty(timeField)) {
				// 如果时间字段名未设定，则默认不要按时间来检索，因为不确定对应的数据库是否存在可用的时间字段
				return null;
			}
		}

		ABuildWhereFieldName ano = m.getAnnotation(ABuildWhereFieldName.class);
		if (ano != null) {
			// 注解上设置的字段名的优先级其次
			return ano.name();
		} else {
			// 如果没用发现任何用注解的定义，就直接用getter名作为字段名
			if (m.getName().startsWith("is")) {
				return StringUtils.uncapitalize(m.getName().substring(2));
			} else {
				return StringUtils.uncapitalize(m.getName().substring(3));
			}
		}
	}

	/**
	 * 获得操作符， 默认是 “=”
	 * 
	 * @param m
	 * @return
	 */
	private String getOptStr(Method m) {
		if (m.isAnnotationPresent(ABuildWhereTimeField.class)) {
			// 如果声明了时时间类型，需要特别处理一下
			ABuildWhereTimeField ano = m.getAnnotation(ABuildWhereTimeField.class);
			if (ano != null) {
				if (ano.isBegin()) {
					return ">=";
				} else {
					return "<=";
				}
			}
		}

		ABuildWhereOptStr ano = m.getAnnotation(ABuildWhereOptStr.class);
		if (ano != null) {
			return ano.optStr();
		} else {
			return "=";
		}
	}

	/**
	 * 获得在参数中值
	 * 
	 * @param m
	 * @return
	 */
	private Object getValueInParams(Method m) {
		try {
			Object value = m.invoke(form);
			if (value != null) {
				if (m.getReturnType() == String.class) {
					// 如果是字符串行，要特别检测一下是否声明是 like的操作符
					ABuildWhereOptStr ano = m.getAnnotation(ABuildWhereOptStr.class);
					if (ano != null && OPT_LIKE.equals(ano.optStr())) {
						String old = (String) value;
						if (StringUtils.isEmpty(old)) {
							// 字符串可能只是空格，如果全是空格，也返回null
							return null;
						}

						// 将所有的"%"去掉，避免用户在输入的字符串中有%号时，可能导致的全表扫描问题
						String res = processLikeParam(old);
						// 迫不得已要做全表索引的时候加这个参数--表设计的问题，我是有苦衷的☺
						if (ano.mustBeAllLike()) {
							res = "%" + processLikeParam(old);
						}
						return res;
					}
				} else if (m.getReturnType() == Date.class) {
					// 如果是时间类型，要特别处理一下是开始还是结束，要处理 00:00:00 23:59:59的问题
					ABuildWhereTimeField ano = m.getAnnotation(ABuildWhereTimeField.class);
					if (ano != null) {
						return DateUtil.ceilDateToDay((Date) value, ano.isBegin());
					}
				}
			}
			return value;
		} catch (Exception e) {
			LogUtil.traceError(log, e);
		}
		return null;
	}

	/**
	 * where条件拼接--方便单表的条件，暂不支持多表
	 * 
	 * TODO 有时间加上多表规则
	 * 
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

		for (Method m : form.getClass().getMethods()) {
			if (this.isIncludeMethod(m)) {

				Object value = this.getValueInParams(m);
				if (value != null && value.toString().trim().length() > 0) {
					String fieldName = this.getFieldName(m);
					if (fieldName != null) {
						String optStr = this.getOptStr(m);

						where.append(" and ");

						if (this.isSetAsName && !fieldName.startsWith("bm_")) {
							where.append(this.asName).append(".");
						}
						where.append(fieldName);

						if (m.getReturnType() == Integer.class
								&& Integer.parseInt(value.toString()) == INT_VALUE_IS_NULL) {
							// 条件为is null时，无参数
							where.append(" is null ");
						} else {
							where.append(" ").append(optStr).append(" ?");

							if (JPA_STYLE) {
								// jpa风格的sql
								where.append(jpaStyleIndex);
								jpaStyleIndex++;
							}

							if (m.getReturnType() == Date.class) {
								// 日期型需要特殊处理
								if (">=".equals(optStr)) {
									this.paramList.add(DateUtil.parserTimestapForDate((Date) value, true));
								} else {
									this.paramList.add(DateUtil.parserTimestapForDate((Date) value, false));
								}
							} else if (m.isAnnotationPresent(ABuildWhereTimeField.class)) {
								// 日期型需要特殊处理
								if (">=".equals(optStr)) {
									this.paramList.add(DateUtil.parserTimestap(value.toString(), true));
								} else {
									this.paramList.add(DateUtil.parserTimestap(value.toString(), false));
								}
							} else {
								this.paramList.add(value);
							}
						}
					}
				}
			}
		}
	}

	public String getWhereSql() {
		return this.where.toString();
	}

	public Object[] getParams() {
		return this.paramList.toArray();
	}

	public List<Object> getParamsList() {
		return this.paramList;
	}

	/** 获取jpa风格的参数序号，并且+1 */
	public int getAndIncreaseJpaStyleIndex() {
		int old = this.jpaStyleIndex;
		this.jpaStyleIndex++;
		return old;
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
	 * 处理一下作为like的参数，主要是替换掉%，并且在最后增加%
	 * 
	 * @param param
	 * @return
	 */
	public static String processLikeParam(String param) {
		Assert.hasText(param, "参数不能为空");
		return param.replace('%', ' ') + "%";
	}

}
