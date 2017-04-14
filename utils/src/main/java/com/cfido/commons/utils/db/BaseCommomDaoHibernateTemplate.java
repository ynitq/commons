package com.cfido.commons.utils.db;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.HibernateTemplate;

import com.cfido.commons.beans.exceptions.DaoException;
import com.cfido.commons.beans.form.IPageForm;
import com.cfido.commons.utils.utils.LogUtil;

/**
 * <pre>
 * 用HibernateTemplate实现的通用dao的基类。和原来不同的地方是用hibernate4
 * </pre>
 * 
 * @author 梁韦江 2016年9月18日
 */
public abstract class BaseCommomDaoHibernateTemplate implements ICommonDao {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BaseCommomDaoHibernateTemplate.class);

	/** 查找数据库所有记录时，默认的记录数限制，避免一次查询数据量太大 */
	public static final int FIND_ALL_LIMIT = 1000;

	/**
	 * 删除po
	 * 
	 * @param po
	 * @throws DaoException
	 */
	@Override
	public void delete(Object po) throws DaoException {
		try {
			this.getHibernateTemplate().delete(po);
		} catch (Throwable e) {
			throw new DaoException(" delete " + po.getClass().getName(), e);
		}
	}

	/**
	 * 用于执行hsql语句
	 * 
	 * @param hsql
	 *            查询语句
	 * @param params
	 *            参数
	 * @return 影响的记录数
	 * @throws DaoException
	 */
	@Override
	public int executeUpdate(String hsql, Object... params) throws DaoException {
		if (log.isDebugEnabled()) {
			log.debug(LogUtil.printSql(hsql, params));
		}

		try {
			return this.getHibernateTemplate().bulkUpdate(hsql, params);
		} catch (Throwable e) {
			throw new DaoException("executeUpdate:" + hsql, e);
		}
	}

	/**
	 * 可用于返回任何类型List的查询
	 * 
	 * @param hsql
	 *            查询语句
	 * @param pageForm
	 *            包含页码和每页长度的form，该参数为空时，表示不分页，查询所有
	 * @param beanClass
	 *            返回的List中的类型
	 * @param params
	 *            其他参数
	 * @return
	 * @throws DaoException
	 */
	@Override
	public <L> List<L> find(final String hsql, final IPageForm pageForm, Class<L> beanClass, final Object... params)
			throws DaoException {
		if (log.isDebugEnabled()) {
			log.debug(LogUtil.printSql(hsql, params));
		}

		try {

			long start = System.currentTimeMillis();
			List<L> result = this.getHibernateTemplate().execute(new HibernateCallback<List<L>>() {

				@SuppressWarnings("unchecked")
				@Override
				public List<L> doInHibernate(Session session) throws HibernateException {
					Query query = session.createQuery(hsql); // 要查询的HQL，定义的时候需要用final修饰

					bindPageForm(query, pageForm);// 设置分页
					bindParam(query, params);// 设置参数

					return query.list();
				}
			});

			long end = System.currentTimeMillis();

			// 如果sql执行超过1秒，则发出警告，提示程序猿检查SQL代码
			if (end - start > 1000) {
				LogUtil.traceWarn(log, "####当前SQL执行时间超出1秒 : " + LogUtil.printSql(hsql, params));
			}

			return result;
		} catch (Throwable e) {
			throw new DaoException(LogUtil.printSql(hsql, params), e);
		}
	}

	/**
	 * 可根据输入的vo的类型，执行分页查询
	 * 
	 * @param sql
	 *            要执行的sql
	 * @param countHsql
	 *            计算总数的sql
	 * @param beanClass
	 *            vo的class
	 * @param params
	 *            参数数组
	 * @param form
	 *            分页表单
	 * @return
	 * @throws DaoException
	 */
	@Override
	public <L> PageQueryResult<L> findInPage(String sqlStartWithFrom, IPageForm form, Class<L> beanClass,
			Object... params)
			throws DaoException {
		try {

			String countHsql = "select count(*) " + sqlStartWithFrom;
			List<L> items = this.find(sqlStartWithFrom, form, beanClass, params);
			int total = this.getCount(countHsql, params);
			return new PageQueryResult<L>(total, items, form);
		} catch (Throwable e) {
			throw new DaoException("findInPage:" + LogUtil.printSql(sqlStartWithFrom, params), e);
		}
	}

	/**
	 * 根据主键获取对象
	 * 
	 * @param poClass
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	@Override
	public <T> T get(Class<T> poClass, Serializable id) throws DaoException {
		try {
			return this.getHibernateTemplate().get(poClass, id);
		} catch (Throwable e) {
			throw new DaoException("get:" + poClass.getName(), e);
		}
	}

	/**
	 * 将sql结果集返回的第一个字段以整数的方式返回，通常用于 select count(*) 这样的语句
	 * 
	 * @param hsql
	 *            select count 开头的sql语句
	 * @param params
	 *            参数
	 * @return
	 * @throws DaoException
	 */
	@Override
	public int getCount(String hsql, Object... params) throws DaoException {
		if (log.isDebugEnabled()) {
			log.debug(LogUtil.printSql(hsql, params));
		}

		try {
			List<?> list = this.getHibernateTemplate().find(hsql, params);
			if (list.size() > 0) {
				Long o = (Long) list.get(0);
				return o.intValue();
			}
			return 0;
		} catch (Throwable e) {
			throw new DaoException("getCount:" + hsql, e);
		}
	}

	/**
	 * 将sql结果级返回的第一个字段以double的方式返回，通常用于 select sum(*) 这样的语句
	 * 
	 * @throws DaoException
	 */
	@Override
	public double getSum(String hsql, Object... params) throws DaoException {
		if (log.isDebugEnabled()) {
			log.debug(LogUtil.printSql(hsql, params));
		}

		try {
			double sum = 0d;
			List<?> list = getHibernateTemplate().find(hsql, params);
			if (list != null && list.size() > 0) {
				Object obj = list.get(0);
				if (obj != null) {
					Double dbl = new Double("" + obj.toString());
					if (dbl != null) {
						sum = dbl.doubleValue();
					}
				}
			}
			return sum;
		} catch (Throwable e) {
			throw new DaoException("getSum:" + hsql, e);
		}
	}

	/**
	 * 新增到到数据库
	 * 
	 * @param po
	 * @throws DaoException
	 */
	@Override
	public void insert(Object po) throws DaoException {
		try {
			this.getHibernateTemplate().save(po);
		} catch (Throwable e) {
			throw new DaoException("insert:" + po.getClass().getName(), e);
		}
	}

	/**
	 * 保存到数据库
	 * 
	 * @param po
	 * @return
	 * @throws DaoException
	 */
	@Override
	public void update(Object po) throws DaoException {
		try {
			this.getHibernateTemplate().update(po);
		} catch (Throwable e) {
			throw new DaoException("update:" + po.getClass().getName(), e);
		}
	}

	private void bindPageForm(Query query, IPageForm pageForm) {
		int pageNo = 0;
		int pageSize = FIND_ALL_LIMIT;

		if (pageForm != null) {
			// 校验一下页面是否合法，整个类中的所有查询方法已经全部放到这个地方了，所以只需要在这个地方校验页面
			pageForm.verifyPageNo();
			pageSize = IPageForm.DEFAULT_PAGE_SIZE;
			if (pageForm != null) {
				if (pageForm.getPageNo() > 1) {
					pageNo = pageForm.getPageNo();

					// 外面的页面是从1开始，但查询的参数是0开始
					pageNo--;
				}

				if (pageForm.getPageSize() > 0) {
					pageSize = pageForm.getPageSize();
				}
			}
		}

		// 无论是否有翻页信息，我们都限制一下查询记录的总数，目前限制是1000条
		query.setFirstResult((pageNo) * pageSize);
		query.setMaxResults(pageSize);
	}

	/**
	 * 将参数设置到查询
	 * 
	 * @param query
	 *            JPA Query
	 * @param args
	 *            参数列表（可变参数）
	 */
	private void bindParam(Query query, Object[] args) {
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i + 1, args[i]);
			}
		}
	}

	/** 由子类实现，用于适用于不同的数据源 */
	protected abstract HibernateTemplate getHibernateTemplate();

	/**
	 * 通过传入的VO转换工场来查询, 并将结果集和分页的数据封装起来
	 * 
	 * @param converter
	 *            对象转换器
	 * @param hsql
	 *            sql
	 * @param countSql
	 *            count的sql
	 * @param params
	 *            参数
	 * @param pageForm
	 *            翻页表单
	 * @return
	 */
	public <V> PageQueryResult<V> findInPageWithObjConverter(IRowToBean<V> converter, String hsql, String countSql,
			Object[] params,
			IPageForm pageForm) {
		int total = this.getCount(countSql, params);

		List<V> list = this.findWithObjConverter(converter, hsql, params, pageForm);
		return new PageQueryResult<V>(total, list, pageForm);
	}

	/**
	 * 通过传入的bean转换器来查询
	 * 
	 * @param converter
	 *            对象转换器
	 * @param hsql
	 *            sql
	 * @param params
	 *            参数
	 * @param pageForm
	 *            翻页表单
	 * @return
	 */
	public <V> List<V> findWithObjConverter(final IRowToBean<V> converter, final String hsql, final Object[] params,
			final IPageForm pageForm) {

		long start = System.currentTimeMillis();

		if (log.isDebugEnabled()) {
			log.debug(LogUtil.printSql(hsql, params));
		}

		List<V> result = this.getHibernateTemplate().execute(new HibernateCallback<List<V>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<V> doInHibernate(Session session) throws HibernateException {
				Query q = session.createQuery(hsql); // 要查询的HQL，定义的时候需要用final修饰

				if (params != null) {
					for (int i = 0; i < params.length; i++) {
						q.setParameter(i, params[i]);
					}

				}

				if (pageForm != null) {
					// 校验一下页面是否合法，整个类中的所有查询方法已经全部放到这个地方了，所以只需要在这个地方校验页面
					pageForm.verifyPageNo();
					int pageNo = 0;
					int pageSize = IPageForm.DEFAULT_PAGE_SIZE;
					if (pageForm != null) {
						if (pageForm.getPageNo() > 1) {
							pageNo = pageForm.getPageNo();

							// 外面的页面是从1开始，但查询的参数是0开始
							pageNo--;
						}

						if (pageForm.getPageSize() > 0) {
							pageSize = pageForm.getPageSize();
						}
					}

					q.setFirstResult((pageNo) * pageSize);
					q.setMaxResults(pageSize);
				}

				List<Object> listResult = q.list();

				List<V> list = new LinkedList<V>();
				for (Object objInRow : listResult) {
					V vo = converter.rowToBean(q, objInRow);
					if (vo != null) {
						list.add(vo);
					} else {
						log.warn("查询结果无法转化成为bean, 转换器:" + converter);
					}
				}

				return list;
			}
		});

		// 如果sql执行超过1秒，则发出警告，提示程序猿检查SQL代码
		long end = System.currentTimeMillis();
		if (end - start > 1000) {
			LogUtil.traceWarn(log, "####当前SQL执行时间超出1秒 : " + hsql);
		}

		return result;
	}

	@Override
	public <L> L findOne(String hsql, Class<L> beanClass, Object... params) throws DaoException {
		List<L> list = this.find(hsql, IPageForm.ONE, beanClass, params);
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

}
