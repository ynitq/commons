package com.cfido.commons.codeGen.convertor;

import com.cfido.commons.codeGen.core.MetadataReader.ColumnInfo;

/**
 * <pre>
 * 将数据库的字段类型转换成为java类
 * </pre>
 * 
 * @author 梁韦江 2016年9月13日
 */
public interface IDataTypeConvertor {

	/**
	 * 转换
	 */
	Class<?> convert(ColumnInfo info);

}
