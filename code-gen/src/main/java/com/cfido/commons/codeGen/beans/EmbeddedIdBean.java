package com.cfido.commons.codeGen.beans;

import java.util.LinkedList;
import java.util.List;

import com.cfido.commons.codeGen.core.MetadataReader.ColumnInfo;

/**
 * <pre>
 * 描述数据库中的一张表
 * </pre>
 * 
 * @author 梁韦江 2016年9月11日
 */
public class EmbeddedIdBean implements IColumnBean {

	private final TableBean table;
	private final List<ColumnBean> columns = new LinkedList<>();

	public EmbeddedIdBean(TableBean table, List<ColumnInfo> pkList) {
		this.table = table;

		for (ColumnInfo info : pkList) {
			this.columns.add(new ColumnBean(table, info));
		}
	}

	public List<ColumnBean> getColumns() {
		return columns;
	}

	public String getTableName() {
		return this.table.getName();
	}

	@Override
	public String getJavaClassName() {
		return this.table.getJavaClassName() + "Id";
	}
}
