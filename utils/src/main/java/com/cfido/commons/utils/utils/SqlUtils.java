package com.cfido.commons.utils.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <pre>
 * 和数据库、sql相关的工具
 * </pre>
 * 
 * @author 梁韦江 2016年9月11日
 */
public class SqlUtils {
	/**
	 * 打印JDBC ResultSet中的头信息
	 * 
	 * <pre>
	 * 格式:序号 名字 三种类型（ 数字、字符串、java类）
	 * </pre>
	 * 
	 * @param resultSet
	 *            ResultSet本身
	 * @throws SQLException
	 */
	public static String printResultSetMetaData(ResultSet resultSet) throws SQLException {
		ResultSetMetaData rsm = resultSet.getMetaData();

		StringBuffer sb = new StringBuffer();
		int columnCount = rsm.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			sb.append(i);
			sb.append("\t").append(rsm.getColumnLabel(i));// 序号
			sb.append("\t").append(rsm.getColumnType(i));
			sb.append("\t").append(rsm.getColumnTypeName(i));
			sb.append("\t").append(rsm.getColumnClassName(i));
			sb.append("\n");
		}

		return sb.toString();
	}

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 将SQL和参数拼接起来。
	 * 
	 * @param hsql
	 *            hibernate的sql语句
	 * @param params
	 *            参数
	 */
	public static String printSql(String hsql, Object[] params) {
		StringBuffer buff = new StringBuffer();

		int index = 0;
		int p = 0;
		char[] ary = hsql.toCharArray();

		while (p < ary.length) {
			char c = ary[p];

			if (c != '?') {
				buff.append(c);
				p++;
			} else {
				if (params == null) {
					throw new RuntimeException("有sql参数数量不够");
				}

				if (index < params.length) {
					Object obj = params[index];
					if (obj instanceof Date) {
						buff.append("'");
						buff.append(sdf.format(obj));
						buff.append("'");
					} else if (obj instanceof String) {
						buff.append("'");
						buff.append(obj);
						buff.append("'");
					} else {
						buff.append(String.valueOf(obj));
					}
				} else {
					throw new RuntimeException("有sql参数数量不够");
				}

				index++;

				p++;

				// 跳过问号后面的数字，jpa的sql例子 :from App where id=?1,
				while (p < ary.length) {
					if (Character.isDigit(ary[p])) {
						p++;
					} else {
						break;
					}
				}
			}
		}
		return buff.toString();
	}

}
