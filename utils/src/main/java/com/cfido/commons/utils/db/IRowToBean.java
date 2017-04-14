package com.cfido.commons.utils.db;

import org.hibernate.Query;

/**
 * <pre>
 * sql的一行到bean的转化器的基类，用于将hsql的执行结果转换成为bean
 * </pre>
 * 
 * @author 梁韦江 2015年8月5日
 */
public interface IRowToBean<T> {

	/**
	 * 子类需实现查询结果中的每行到VO的转换
	 * 
	 * @param hibernateQuery
	 * @param objFromListRow
	 *            query.list中的一行
	 * @return
	 */
	T rowToBean(Query hibernateQuery, Object objFromListRow);

}
