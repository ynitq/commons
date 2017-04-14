package com.cfido.commons.utils.db;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.cfido.commons.beans.exceptions.DaoException;
import com.cfido.commons.beans.form.IPageForm;
import com.cfido.commons.utils.utils.LogUtil;

/**
 * <pre>
 * 用EntityManager实现的通用Dao基类。
 * 
 * 这个类有个大问题，不能直接使用有写操作的方法，只能由带了事务注解的Service来调用，例如：
 * 
 * &#64;Transactional
 * &#64;Service
 * public class SomeService {
 * }
 * 
 * </pre>
 * 
 * @author 梁韦江 2016年8月18日
 */
public abstract class BaseCommomDaoEntityManager implements ICommonDao {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BaseCommomDaoEntityManager.class);

	/** 查找数据库所有记录时，默认的记录数限制，避免一次查询数据量太大 */
	public static final int FIND_ALL_LIMIT = 2000;

	/**
	 * 删除po
	 * 
	 * @param po
	 * @throws DaoException
	 */
	@Override
	public void delete(Object po) throws DaoException {
		try {
			this.checkTransactionForWrite();

			// 删除前需要先merge，防止原Session已经消失，然后删除返回出来的对象
			Object poForDelete = this.getEntityManager().merge(po);

			this.getEntityManager().remove(poForDelete);
		} catch (Throwable e) {
			throw new DaoException("删除时发生了错误 " + po.getClass().getName() + " 错误信息:" + e.getMessage(), e);
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
			this.checkTransactionForWrite();
			Query query = this.getEntityManager().createQuery(hsql);
			bindParam(query, params);
			return query.executeUpdate();
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
	public <L> List<L> find(String hsql, IPageForm pageForm, Class<L> beanClass, Object... params) throws DaoException {
		if (log.isDebugEnabled()) {
			log.debug(LogUtil.printSql(hsql, params));
		}

		try {

			long start = System.currentTimeMillis();
			TypedQuery<L> query = this.getEntityManager().createQuery(hsql, beanClass);
			this.bindParam(query, params);
			this.bindPageForm(query, pageForm);

			List<L> result = query.getResultList();
			long end = System.currentTimeMillis();

			// 如果sql执行超过1秒，则发出警告，提示程序猿检查SQL代码
			if (end - start > 1000) {
				log.warn(LogUtil.getStackTrace("####当前SQL执行时间超出1秒 : " + LogUtil.printSql(hsql, params)));
			}

			return result;
		} catch (Throwable e) {
			throw new DaoException(LogUtil.printSql(hsql, params), e);
		}
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
			return new PageQueryResult<>(total, items, form);
		} catch (Throwable e) {
			throw new DaoException("findInPage:" + sqlStartWithFrom, e);
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
			return this.getEntityManager().find(poClass, id);
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
			Query query = this.getEntityManager().createQuery(hsql);
			bindParam(query, params);

			Object firstRow = query.getSingleResult();

			if (firstRow != null) {

				return Integer.valueOf(firstRow.toString());
			} else {
				return 0;
			}
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
			Query query = this.getEntityManager().createQuery(hsql);
			bindParam(query, params);

			Object firstRow = query.getSingleResult();
			if (firstRow != null) {
				sum = Double.valueOf(firstRow.toString());
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
			this.checkTransactionForWrite();
			this.getEntityManager().persist(po);
			this.getEntityManager().flush();
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
			this.checkTransactionForWrite();
			this.getEntityManager().merge(po);
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
			pageSize = this.getDefaultPageSize();
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

	/**
	 * 执行写操作的时候，需要加入数据库事务
	 */
	private void checkTransactionForWrite() {
		if (!this.getEntityManager().isJoinedToTransaction()) {
			// log.debug("加入 数据库事物");
			this.getEntityManager().joinTransaction();
		}
	}

	/**
	 * 获得指定数据源的
	 * 
	 * <pre>
	 * 例子：
	 * &#64;PersistenceContext(unitName = RepoForOldConfig.ENTITY_MANAGER_FACTORY)
	 * </pre>
	 */
	protected abstract EntityManager getEntityManager();

	public void clearEntityManager() {
		this.getEntityManager().clear();
	}

	/**
	 * 默认每行的记录数
	 */
	protected int getDefaultPageSize() {
		return IPageForm.DEFAULT_PAGE_SIZE;
	}

}
