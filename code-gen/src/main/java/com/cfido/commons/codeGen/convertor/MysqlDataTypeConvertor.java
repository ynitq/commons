package com.cfido.commons.codeGen.convertor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.TemporalType;

import com.cfido.commons.codeGen.core.MetadataReader.ColumnInfo;

/**
 * <pre>
 * 数据库类型-> java类型的转换
 * </pre>
 * 
 * @author 梁韦江 2016年9月13日
 */
public class MysqlDataTypeConvertor implements IDataTypeConvertor {

	public static MysqlDataTypeConvertor getInstance() {
		return instance;
	}

	class StringT extends BaseSubDataTypeConvertor {

		@Override
		public Class<?> convert(ColumnInfo info) {
			return String.class;
		}

		@Override
		public String[] getDataTypes() {
			return new String[] {
					"varchar",
					"text",
					"mediumtext",
					"tinytext",
					"char",
					"longtext",
			};
		}
	}

	class DateT extends BaseSubDataTypeConvertor {

		@Override
		public Class<?> convert(ColumnInfo info) {
			return Date.class;
		}

		@Override
		public String[] getDataTypes() {
			return new String[] {
					"date",
					"timestamp",
					"datetime",
					"year",
					"time",
			};
		}
	}

	class BooleanT extends BaseSubDataTypeConvertor {

		@Override
		public Class<?> convert(ColumnInfo info) {
			return info.isNullable() ? Boolean.class : boolean.class;
		}

		@Override
		public String[] getDataTypes() {
			return new String[] {
					"bool",
					"boolean",
					"bit"
			};
		}
	}

	class DoubleT extends BaseSubDataTypeConvertor {

		@Override
		public Class<?> convert(ColumnInfo info) {
			return info.isNullable() ? Double.class : double.class;
		}

		@Override
		public String[] getDataTypes() {
			return new String[] {
					"decimal",
					"double",
					"float",
					"numeric",
					"real",
			};
		}
	}

	class ByteArrayT extends BaseSubDataTypeConvertor {

		@Override
		public Class<?> convert(ColumnInfo info) {
			return byte[].class;
		}

		@Override
		public String[] getDataTypes() {
			return new String[] {
					"binary",
					"blob",
					"longblob",
					"mediumblob",
					"tinyblob",
					"varbinary",
			};
		}
	}

	class NumberT extends BaseSubDataTypeConvertor {

		@Override
		public Class<?> convert(ColumnInfo info) {
			boolean nullable = info.isNullable();

			if ("tinyint(1)".equals(info.getColumnType())) {
				// tinyint(1) 是 boolean 型的，这个特殊
				if (nullable) {
					// 如果允许null
					return Boolean.class;
				} else {
					return boolean.class;
				}
			}

			if (info.getNumLen() > 10) {
				// 超过11位的，只能用Long型，其实这个有问题，Integer 最大值是 +/-2147,483,647, 其实不够位数
				return nullable || info.isPrimaryKey() ? Long.class : long.class;
			} else {
				return nullable || info.isPrimaryKey() ? Integer.class : int.class;
			}
		}

		@Override
		public String[] getDataTypes() {
			return new String[] {
					"int",
					"bigint",
					"mediumint",
					"smallint",
					"tinyint",
			};
		}
	}

	private final static MysqlDataTypeConvertor instance = new MysqlDataTypeConvertor();

	private final Map<String, IDataTypeConvertor> transMap = new HashMap<>();
	private final Map<String, TemporalType> dateTypeMap = new HashMap<>();

	private MysqlDataTypeConvertor() {
		this.addConvertor(new StringT());// 大多数String行的
		this.addConvertor(new DateT());// 日期型
		this.addConvertor(new NumberT());// 数字型
		this.addConvertor(new BooleanT());// bool型
		this.addConvertor(new DoubleT());
		this.addConvertor(new ByteArrayT());

		this.dateTypeMap.put("datetime", TemporalType.TIMESTAMP);
		this.dateTypeMap.put("date", TemporalType.DATE);
		this.dateTypeMap.put("year", TemporalType.DATE);
		this.dateTypeMap.put("time", TemporalType.TIME);
		this.dateTypeMap.put("timestamp", TemporalType.TIMESTAMP);

	}

	public void addConvertor(BaseSubDataTypeConvertor tran) {
		for (String key : tran.getDataTypes()) {
			this.transMap.put(key, tran);
		}
	}

	@Override
	public Class<?> convert(ColumnInfo info) {
		IDataTypeConvertor tran = this.transMap.get(info.getDataType());
		if (tran != null) {
			return tran.convert(info);
		} else {
			String msg = String.format("数据类型:%s 找不到相应的Java类型匹配，请通过 addConvertor 增加类型转换器",
					info.getColumnName());
			throw new RuntimeException(msg);
		}
	}

	public TemporalType dataTypeToTemporalType(String dataType) {
		return this.dateTypeMap.get(dataType);
	}
}
