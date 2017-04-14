package com.cfido.commons.utils.db;

import java.io.Serializable;
import java.util.List;

import com.cfido.commons.beans.exceptions.DaoException;
import com.cfido.commons.beans.form.IPageForm;

/**
 * <pre>
 * 通用dao接口，我们有hibernate4和jpa两种实现方式
 * </pre>
 * 
 * @author 梁韦江 2016年9月19日
 */
public interface ICommonDao {

	/**
	 * 更新对象
	 */
	void update(Object po) throws DaoException;

	/**
	 * 插入对象
	 */
	void insert(Object po) throws DaoException;

	/**
	 * 计算 sum
	 * 
	 * @param hsql
	 *            sql语句
	 * @param params
	 *            参数
	 * @return double型的结果
	 * @throws DaoException
	 */
	double getSum(String hsql, Object... params) throws DaoException;

	/**
	 * 计算 count
	 * 
	 * @param hsql
	 *            sql语句
	 * @param params
	 *            sql中的参数
	 * @return 计算结果
	 * @throws DaoException
	 */
	int getCount(String hsql, Object... params) throws DaoException;

	/**
	 * 根据主键获取数据
	 * 
	 * @param poClass
	 *            po的类型
	 * @param id
	 *            主键
	 * @return
	 * @throws DaoException
	 */
	<T> T get(Class<T> poClass, Serializable id) throws DaoException;

	/**
	 * 分页查询
	 * 
	 * @param sqlStartWithFrom
	 *            from开头的sql
	 * @param form
	 *            分页表单
	 * @param beanClass
	 *            po的类型
	 * @param params
	 *            查询的参数
	 * @return 分页查询封装
	 * @throws DaoException
	 */
	<L> PageQueryResult<L> findInPage(String sqlStartWithFrom, IPageForm form, Class<L> beanClass,
			Object... params) throws DaoException;

	/**
	 * 查询
	 * 
	 * @param sqlStartWithFrom
	 *            rom开头的sql
	 * @param pageForm
	 *            分页表单，如果为空就表示全部
	 * @param beanClass
	 *            PO的类型
	 * @param params
	 *            查询的参数
	 * @return list
	 * @throws DaoException
	 */
	<L> List<L> find(final String sqlStartWithFrom, final IPageForm pageForm, Class<L> beanClass, final Object... params)
			throws DaoException;

	/**
	 * 执行update或delete操作，这个方法最好少用
	 * 
	 * @param hsql
	 *            update或者delete的语句
	 * @param params
	 *            参数
	 * @return 收到影响的记录数
	 * @throws DaoException
	 */
	int executeUpdate(String hsql, Object... params) throws DaoException;

	/**
	 * 删除一个对象
	 * 
	 * @param po
	 *            PO的类型
	 * @throws DaoException
	 */
	void delete(Object po) throws DaoException;

	public <L> L findOne(String hsql, Class<L> beanClass, Object... params) throws DaoException;
}
