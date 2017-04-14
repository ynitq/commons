package com.cfido.commons.codeGen.convertor;

/**
 * <pre>
 * 数据库字段转java类型的转换器基类
 * </pre>
 * 
 * @author 梁韦江 2016年9月14日
 */
public abstract class BaseSubDataTypeConvertor implements IDataTypeConvertor {

	/**
	 * 指定那些数据类型通过这个转换器来转换成为java类
	 */
	public abstract String[] getDataTypes();

}
