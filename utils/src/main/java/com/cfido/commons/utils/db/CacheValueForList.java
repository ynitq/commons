package com.cfido.commons.utils.db;

import java.util.List;

/**
 * <pre>
 * 用于将list保存在cache中的bean
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月18日
 */
public class CacheValueForList<ID> {
	/** sql + 参数 */
	private String fullSql;

	/** 翻页信息 */
	private String pageStr;

	private List<ID> list;

	public List<ID> getList() {
		return list;
	}

	public void setList(List<ID> list) {
		this.list = list;
	}

	public String getFullSql() {
		return fullSql;
	}

	public void setFullSql(String fullSql) {
		this.fullSql = fullSql;
	}

	public String getPageStr() {
		return pageStr;
	}

	public void setPageStr(String pageStr) {
		this.pageStr = pageStr;
	}
}