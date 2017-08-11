package com.cfido.commons.codeGen.beans;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.cfido.commons.codeGen.convertor.MysqlDataTypeConvertor;
import com.cfido.commons.codeGen.core.MetadataReader.ColumnInfo;
import com.cfido.commons.utils.utils.MBeanUtils.IgnoreWhenObjectToMap;

/**
 * <pre>
 * 一个表中的一列的属性
 * </pre>
 * 
 * @author 梁韦江 2016年9月11日
 */
public abstract class BaseColumnVo implements IColumnBean {

	protected final TableBean table;
	protected final ColumnInfo info;

	private final String propName;
	private final String propNameU;

	private final Class<?> javaClass;

	private final FormPropBean form = new FormPropBean();

	/** 备注内容 */
	private final String comment;

	/** 如果这个字段有外键，这里是外键对应的表 */
	private TableBean refTable;

	/** 目前只有mysql的数据类型转换器 */
	private final MysqlDataTypeConvertor dataTypeConvertor = MysqlDataTypeConvertor.getInstance();

	public BaseColumnVo(TableBean table, ColumnInfo info) {
		Assert.notNull(table, "table不能为空");
		Assert.notNull(info, "字段信息不能为空");

		// 原始数据
		this.table = table;
		this.info = info;

		// 驼峰结构的属性名
		this.propName = com.cfido.commons.utils.utils.StringUtilsEx.toUpperCamelCase(info.getColumnName(), false);
		this.propNameU = StringUtils.capitalize(propName);

		// 根据数据库信息，将字段属性转换为java的类
		this.javaClass = dataTypeConvertor.convert(this.info);

		// 解析数据库中的字段说明，分析其中的表单设置
		this.comment = this.form.parserComment(this.info.getComment());

		if (!this.javaClass.isPrimitive() && !this.javaClass.isArray()) {
			this.table.addImport(this.javaClass);
		}

		// 如果是事件类型，需要增加特别的import
		if (this.isTemporal()) {
			this.table.addImport(Temporal.class);
			this.table.addImport(TemporalType.class);
		}
	}

	/**
	 * 获得备注
	 */
	public String getComment() {
		return this.comment;
	}

	/**
	 * 获得Getter， 主要是boolean型的getter是is开头的
	 */
	public String getGetter() {
		if (boolean.class.equals(this.javaClass) || Boolean.class.equals(this.javaClass)) {
			return "is" + this.propNameU;
		} else {
			return "get" + this.propNameU;
		}
	}

	public Class<?> getJavaClass() {
		return javaClass;
	}

	@Override
	public String getJavaClassName() {
		return javaClass.getSimpleName();
	}

	/**
	 * 长度的字符串，如果是String型才有这个属性，否则都是空
	 */
	public String getLengthStr() {
		int len = this.info.getStringLen();

		if (len > 0) {
			return ", length = " + len;
		} else {
			return "";
		}
	}

	/**
	 * 获得数据库的字段名
	 */
	public String getName() {
		return this.info.getColumnName();
	}

	public String getNullableStr() {
		if (this.info.isNullable()) {
			return " , nullable = true";
		} else {
			return "";
		}
	}

	/**
	 * 获得java类的属性名
	 */
	public String getPropName() {
		return propName;
	}

	public String getPropNameU() {
		return propNameU;
	}

	/**
	 * 获得关联表
	 */
	public TableBean getRefTable() {
		return refTable;
	}

	/**
	 * 获得所在的表
	 */
	@IgnoreWhenObjectToMap()
	public TableBean getTable() {
		return this.table;
	}

	/**
	 * 获得所在的表的java名
	 */
	public String getTableJavaName() {
		return this.table.getJavaClassName();
	}

	/**
	 * data类型才有的注解
	 */
	public String getTemporalStr() {
		if (this.isTemporal()) {
			TemporalType type = this.dataTypeConvertor.dataTypeToTemporalType(this.info.getDataType());
			if (type != null) {
				return String.format("@Temporal(TemporalType.%s)", type.name());
			}
		}
		return "";
	}

	/**
	 * 是否有备注
	 */
	public boolean isHasComment() {
		return StringUtils.hasText(this.comment);
	}

	/**
	 * 是否允许为空，并且不能是 int等内置的类型
	 */
	public boolean isNullable() {
		return this.info.isNullable();
	}

	/**
	 * 是否有NotNull注解, 非空，并且是对象类型
	 */
	public boolean isHasNotNull() {
		return !this.info.isNullable() && !this.javaClass.isPrimitive();
	}

	/**
	 * 是否是日期类型，需要@Temporal注解
	 */
	public boolean isTemporal() {
		return Date.class.equals(this.javaClass);
	}

	public void setRefTable(TableBean refTable) {
		Assert.notNull(refTable, "关联表不能为空");

		this.refTable = refTable;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("属性:").append(getName());

		return sb.toString();
	}

	/**
	 * 用于生成Form的各类定义
	 */
	public FormPropBean getForm() {
		return form;
	}

	public String getJavaClassNameInEditForm() {
		if (this.form.isUploadFile()) {
			return "MultipartFile";
		} else {
			return this.getJavaClassName();
		}
	}

}
