package com.cfido.commons.codeGen.beans;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import com.cfido.commons.codeGen.core.MetadataReader.ColumnInfo;

/**
 * <pre>
 * 单一字段的组件
 * </pre>
 * 
 * @author 梁韦江 2016年9月11日
 */
public class SimpleIdBean extends BaseColumnVo {

	private final String generatedValueStr;

	public SimpleIdBean(TableBean table, ColumnInfo info) {
		super(table, info);

		this.generatedValueStr = this.buildGeneratedValueStr();
	}

	private String buildGeneratedValueStr() {
		if ("auto_increment".equals(info.getExtra())) {
			// 对应自增长的ID,需要加 @GeneratedValue 注解
			this.table.addImport(GeneratedValue.class);
			this.table.addImport(GenerationType.class);

			return "@GeneratedValue(strategy = GenerationType.AUTO)";
		} else {
			return "";
		}
	}

	public String getGeneratedValueStr() {
		return generatedValueStr;
	}

}
