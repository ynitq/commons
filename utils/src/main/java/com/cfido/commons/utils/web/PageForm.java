package com.cfido.commons.utils.web;

import java.io.Serializable;

import com.cfido.commons.annotation.form.ABuildWhereExclude;
import com.cfido.commons.annotation.form.AFormValidateMethod;
import com.cfido.commons.beans.form.IPageForm;
import com.cfido.commons.utils.db.WhereBuilder;

public class PageForm implements IPageForm,Serializable {

	private static final long serialVersionUID = 1L;
	
	private int pageNo = 1;
	private String actionName;
	private int pageSize = IPageForm.DEFAULT_PAGE_SIZE;

	/**
	 * where条件拼接--方便单表的条件，暂不支持多表
	 * 
	 * @param timeField
	 *            时间对应的表字段名
	 * @param asName
	 *            as的名字
	 * @return
	 */
	public WhereBuilder buildWhereAndParams(String timeField, String asName) {
		WhereBuilder builder = WhereBuilder.create(this, timeField, asName);
		return builder;
	}

	@ABuildWhereExclude
	public String getActionName() {
		return actionName;
	}

	@Override
	@ABuildWhereExclude
	public int getPageNo() {
		return pageNo;
	}

	@Override
	@ABuildWhereExclude
	public int getPageSize() {
		return pageSize;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	@AFormValidateMethod
	public void verifyPageNo() {
		if (this.pageNo < 1) {
			this.pageNo = 1;
		}
		if (this.pageSize < 1) {
			this.pageSize = 1;
		}
	}

}
