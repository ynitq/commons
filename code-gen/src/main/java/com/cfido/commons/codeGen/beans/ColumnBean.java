package com.cfido.commons.codeGen.beans;

import com.cfido.commons.codeGen.core.MetadataReader.ColumnInfo;

/**
 * <pre>
 * 一个表中的一列的属性
 * </pre>
 * 
 * @author 梁韦江 2016年9月11日
 */
public class ColumnBean extends BaseColumnVo {

	public ColumnBean(TableBean table, ColumnInfo info) {
		super(table, info);
	}
}
