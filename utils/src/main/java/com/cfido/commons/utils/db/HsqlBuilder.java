package com.cfido.commons.utils.db;

import java.util.LinkedList;
import java.util.List;

import com.cfido.commons.utils.utils.StringUtils;

/**
 * 用于构建hsql的帮助类，更加参数数量的不同，生成带和参数数量一样的？的hsql
 * 
 * @author liangwj
 * 
 */
public class HsqlBuilder {
	private final StringBuffer sql = new StringBuffer(100);
	private boolean hasWhere;
	private final List<Object> paramList = new LinkedList<Object>();

	public HsqlBuilder(String baseSql) {
		this.sql.append(baseSql);
		this.hasWhere = false;
	}

	public HsqlBuilder(String baseSql, boolean requestAdd) {
		this.sql.append(baseSql);
		this.hasWhere = requestAdd;
	}

	public void addNoEmpty(String str, String param) {
		if (StringUtils.isNotEmpty(param)) {
			this.checkWhere();
			sql.append(str);
			this.paramList.add(param);
		}
	}

	public void addNoEmpty(String str, Object param) {
		if (param != null) {
			this.checkWhere();
			sql.append(str);
			this.paramList.add(param);
		}
	}

	public void addDate(String str, java.util.Date param, boolean begin) {
		if (param != null) {
			java.util.Date date = com.cfido.commons.utils.utils.DateUtil.ceilDateToDay(param, begin);
			this.checkWhere();
			sql.append(str);
			this.paramList.add(date);
		}
	}

	public void addNoZero(String str, int param) {
		if (param != 0) {
			this.checkWhere();
			sql.append(str);
			this.paramList.add(param);
		}
	}

	public void addNoZero(String str, float param) {
		if (param != 0) {
			this.checkWhere();
			sql.append(str);
			this.paramList.add(param);
		}
	}

	public void addNoZero(String str, double param) {
		if (param != 0) {
			this.checkWhere();
			sql.append(str);
			this.paramList.add(param);
		}
	}
	public void addBoolean(String str, boolean param) {
			this.checkWhere();
			sql.append(str);
			this.paramList.add(param);
	}

	public void checkWhere() {
		if (this.hasWhere) {
			sql.append(" and ");
		} else {
			sql.append(" where ");
		}
		this.hasWhere = true;
	}

	/**
	 * 获得sql
	 * 
	 * @return
	 */
	public String getSql() {
		return this.sql.toString();
	}

	/**
	 * 是否没有参数
	 * 
	 * @return
	 */
	public boolean isParamEmpty() {
		return this.paramList.isEmpty();
	}

	/**
	 * 获得参数的数组
	 * 
	 * @return
	 */
	public Object[] getParams() {
		return this.paramList.toArray();
	}

	public void appendSql(String str) {
		this.sql.append(str);
	}
}
