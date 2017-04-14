package com.cfido.commons.codeGen.beans;

import com.cfido.commons.codeGen.core.MetadataReader.ForeignKeyInfo;

/**
 * <pre>
 * 外键的VO
 * </pre>
 * 
 * @author 梁韦江 2016年9月12日
 */
public class ForeignKeyBean {

	private final String columnName;
	private final TableBean refTable;
	private final String refColumnName;

	public ForeignKeyBean(TableBean refTable, ForeignKeyInfo fkInfo) {
		super();
		this.columnName = fkInfo.getColumnName();
		this.refTable = refTable;
		this.refColumnName = fkInfo.getRefColnumName();
	}

	protected String getColumnName() {
		return columnName;
	}

	protected TableBean getRefTable() {
		return refTable;
	}

	protected String getRefColumnName() {
		return refColumnName;
	}

}
