package com.cfido.commons.codeGen.constants;

/**
 * <pre>
 * 用于生成 form
 * 
 * 如果数据库的字段备注中有下面的字样，表示该字段要出现在相应的form中。
 * 
 * 分隔符为“|”， 例如 LF|EF|法人名字
 * 
 * 注意：
 * 如果不需要list，则修改、查看、删除等操作都不需要了
 * </pre>
 * 
 * @author 梁韦江 2016年10月11日
 */
public enum FormPropEnum {
	EF, // 出现在edit表单中，根据字段属性智能判断

	FILE, // 保存的是上传文件路径

	LF,// 出现在查询表单ListForm中，根据字段属性自动判断

}
