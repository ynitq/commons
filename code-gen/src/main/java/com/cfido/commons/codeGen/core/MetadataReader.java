package com.cfido.commons.codeGen.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * <pre>
 * 读取数据库数据
 * </pre>
 * 
 * @author 梁韦江 2016年9月11日
 */
public class MetadataReader {

	/**
	 * <pre>
	 * 表中每一个字段的信息
	 * </pre>
	 * 
	 * @author 梁韦江 2016年9月12日
	 */
	public class ColumnInfo {
		private String columnName;
		private String nullable;
		private String dataType;
		private String lenStr;
		private String numLenStr;
		private String numScaleStr;
		private String extra;
		private String comment;
		private String columnType;

		private boolean primaryKey;

		public String getColumnName() {
			return columnName;
		}

		public String getComment() {
			return comment;
		}

		public boolean isPrimaryKey() {
			return primaryKey;
		}

		public void setPrimaryKey(boolean primaryKey) {
			this.primaryKey = primaryKey;
		}

		public String getDataType() {
			return dataType;
		}

		public String getExtra() {
			return extra;
		}

		public int getNumLen() {
			try {
				return Integer.parseInt(this.numLenStr);
			} catch (NumberFormatException ex) {
				return 0;
			}
		}

		public String getColumnType() {
			return columnType;
		}

		/**
		 * 小数点位数
		 */
		public int getNumScale() {
			try {
				return Integer.parseInt(this.numScaleStr);
			} catch (NumberFormatException ex) {
				return 0;
			}
		}

		public int getStringLen() {
			try {
				return Integer.parseInt(this.lenStr);
			} catch (NumberFormatException ex) {
				return 0;
			}
		}

		public boolean isNullable() {
			return "YES".equalsIgnoreCase(this.nullable);
		}

	}

	/**
	 * <pre>
	 * 外键的信息
	 * </pre>
	 * 
	 * @author 梁韦江 2016年9月12日
	 */
	public class ForeignKeyInfo {
		private String columnName;
		private String refTableName;
		private String refColnumName;

		private ForeignKeyInfo() {
		}

		public String getColumnName() {
			return columnName;
		}

		public String getRefColnumName() {
			return refColnumName;
		}

		public String getRefTableName() {
			return refTableName;
		}
	}

	public class TableInfo {
		private final String name;
		private final String comment;
		private List<String> primaryKeys;
		private List<ForeignKeyInfo> fkInfoList;
		private List<ColumnInfo> columnInfoList;

		public TableInfo(String name, String comment) {
			super();
			this.name = name;
			this.comment = comment;
		}

		public List<ColumnInfo> getColumnInfoList() {
			return columnInfoList;
		}

		public String getComment() {
			return comment;
		}

		public List<ForeignKeyInfo> getFkInfoList() {
			return fkInfoList;
		}

		public String getName() {
			return name;
		}

		public List<String> getPrimaryKeys() {
			return primaryKeys;
		}

	}

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MetadataReader.class);

	@Autowired
	private CodeGenContext context;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private String catalog;

	private TableInfo mapRowForTable(ResultSet rs) throws SQLException {
		String name = rs.getString("TABLE_NAME");
		String comment = rs.getString("TABLE_COMMENT");

		TableInfo table = new TableInfo(name, comment);// 获得名字和备注

		// 查找主键字段
		table.primaryKeys = this.readPrimaryKeyInfo(table.name);
		// 外键字段
		table.fkInfoList = this.readForeignKeyInfo(table.name);
		// 所有字段
		table.columnInfoList = this.readColumnInfo(table.name);

		return table;
	}

	/**
	 * 读取表中所有列的信息
	 * 
	 * SHOW FULL FIELDS FROM `database`.`tablename`;
	 * 
	 * @param table
	 * @throws SQLException
	 */
	private List<ColumnInfo> readColumnInfo(String tableName) throws SQLException {

		String sql = String.format("SELECT "
				+ " c.`COLUMN_NAME`,c.`IS_NULLABLE`,c.`DATA_TYPE`, "
				+ " c.`CHARACTER_MAXIMUM_LENGTH`,c.`NUMERIC_PRECISION`, "
				+ " c.`NUMERIC_SCALE`, c.`EXTRA`,c.`COLUMN_COMMENT`,c.`COLUMN_TYPE`"
				+ "FROM INFORMATION_SCHEMA.`COLUMNS` c "
				+ "WHERE c.`TABLE_NAME`='%s' "
				+ "  AND c.`TABLE_SCHEMA`='%s' "
				+ "ORDER BY c.`ORDINAL_POSITION`", tableName, this.catalog);

		List<ColumnInfo> columns = this.jdbcTemplate.query(sql, new RowMapper<ColumnInfo>() {
			@Override
			public ColumnInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				ColumnInfo info = new ColumnInfo();
				info.columnName = rs.getString("COLUMN_NAME");
				info.nullable = rs.getString("IS_NULLABLE");
				info.dataType = rs.getString("DATA_TYPE");
				info.lenStr = rs.getString("CHARACTER_MAXIMUM_LENGTH");
				info.numLenStr = rs.getString("NUMERIC_PRECISION");
				info.numScaleStr = rs.getString("NUMERIC_SCALE");
				info.extra = rs.getString("EXTRA");
				info.comment = rs.getString("COLUMN_COMMENT");
				info.columnType = rs.getString("COLUMN_TYPE");

				return info;
			}
		});

		return columns;
	}

	/**
	 * 获取表的外键
	 * 
	 * @param table
	 * @return
	 */
	private List<ForeignKeyInfo> readForeignKeyInfo(String tableName) {

		String sql = String.format("SELECT k.`COLUMN_NAME`, k.`REFERENCED_TABLE_NAME`,k.`REFERENCED_COLUMN_NAME`" +
				" FROM information_schema.`KEY_COLUMN_USAGE` k " +
				" WHERE k.`TABLE_NAME`='%s'  AND k.`TABLE_SCHEMA`='%s' " +
				" AND k.`REFERENCED_TABLE_NAME` IS NOT NULL ",
				tableName, this.catalog);

		List<ForeignKeyInfo> columns = this.jdbcTemplate.query(sql, new RowMapper<ForeignKeyInfo>() {
			@Override
			public ForeignKeyInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				ForeignKeyInfo info = new ForeignKeyInfo();
				info.columnName = rs.getString("COLUMN_NAME");
				info.refTableName = rs.getString("REFERENCED_TABLE_NAME");
				info.refColnumName = rs.getString("REFERENCED_COLUMN_NAME");

				return info;
			}
		});

		return columns;

	}

	private void readTableInfo() throws SQLException {
		String sql = String.format("SELECT c.`TABLE_NAME`, c.`TABLE_COMMENT` " +
				" FROM INFORMATION_SCHEMA.TABLES c" +
				" WHERE c.TABLE_SCHEMA='%s'", this.catalog);

		List<TableInfo> tableInfoList = this.jdbcTemplate.query(sql, new RowMapper<TableInfo>() {
			@Override
			public TableInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				return MetadataReader.this.mapRowForTable(rs);
			}
		});

		for (TableInfo tableInfo : tableInfoList) {
			// 加到context中
			this.context.addTable(tableInfo);
		}

		// 设置完原始数据后，初始化所有的VO
		this.context.afterMetadataReaded();
	}

	/**
	 * 查找一个表的主键字段
	 * 
	 * @param table
	 *            表
	 * @return List<String>，字段的列表
	 * @throws SQLException
	 */
	private List<String> readPrimaryKeyInfo(String tableName) throws SQLException {
		String sql = String.format("SELECT k.`COLUMN_NAME`" +
				" FROM INFORMATION_SCHEMA.`STATISTICS` k " +
				" WHERE k.`TABLE_NAME`='%s' " +
				" AND k.`TABLE_SCHEMA`='%s' " +
				" AND k.`INDEX_NAME`='PRIMARY' ", tableName, this.catalog);

		List<String> columns = this.jdbcTemplate.query(sql, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("COLUMN_NAME");
			}
		});

		return columns;

	}

	public void init() throws SQLException {
		log.debug("初始化 Entity 生成器");
		// 先重置表的数据，应该找个方法是可以重复执行的
		this.context.resetTables();

		this.catalog = this.jdbcTemplate.getDataSource().getConnection().getCatalog();

		this.readTableInfo();
	}

	protected String getCatalog() {
		return catalog;
	}

}
