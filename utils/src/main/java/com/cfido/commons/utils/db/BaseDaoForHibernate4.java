package com.cfido.commons.utils.db;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.cfido.commons.annotation.other.AQCache;
import com.cfido.commons.beans.form.IPageForm;
import com.cfido.commons.utils.utils.ClassUtil;
import com.cfido.commons.utils.utils.LogUtil;

/**
 * Hibernate4 DAO实现基类
 * 
 * @author mmjbds999
 * 
 * @param <E>
 * @param <PK>
 */
public abstract class BaseDaoForHibernate4<E, PK extends Serializable> {

	class StringGetterEn {
		private final Method getter;
		private Method setter;
		private final int length;

		private StringGetterEn(Method getter, int length) {
			super();
			this.getter = getter;
			this.length = length;
		}

		@Override
		public String toString() {
			return String.format("setter:%s getter:%s 长度:%d",
					this.setter.getName(), this.getter.getName(), this.length);
		}
	}

	protected final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

	protected final Class<E> entityClass;
	protected final Class<PK> idClass;
	protected String idName;
	protected final List<String> foreignKeyList = new LinkedList<>();
	private final String fetchSql;

	/** id是否是复合类型 */
	protected final boolean embeddableId;

	/** 是否开启查询缓存 */
	protected final boolean isUseCache;

	protected final List<StringGetterEn> getterList = new LinkedList<>();

	/**
	 * 构造函数--用于初始化相关参数
	 */
	@SuppressWarnings("unchecked")
	public BaseDaoForHibernate4() {
		this.entityClass = (Class<E>) ClassUtil.getGenericType(this.getClass(),
				0);
		this.idClass = (Class<PK>) ClassUtil.getGenericType(this.getClass(), 1);
		this.embeddableId = idClass.getAnnotation(Embeddable.class) != null;

		Map<String, Field> fieldMap = new HashMap<>();
		for (Field f : this.entityClass.getFields()) {
			String fieldName = f.getName();
			fieldMap.put(fieldName, f);
			if (f.getAnnotation(Id.class) != null) {
				this.idName = fieldName;
			}
			if (f.getAnnotation(ManyToOne.class) != null) {
				this.foreignKeyList.add(fieldName);
			}
		}

		if (log.isDebugEnabled()) {
			if (!this.foreignKeyList.isEmpty()) {
				StringBuffer sb = new StringBuffer(255);
				sb.append("表 ");
				sb.append(this.entityClass.getSimpleName());
				sb.append(" 有外键:");
				for (String name : this.foreignKeyList) {
					sb.append(name);
					sb.append(" ");
				}
				log.debug(sb.toString());
			}
		}

		Map<String, Method> methodMap = new HashMap<>();
		for (Method m : this.entityClass.getMethods()) {
			String methodName = m.getName();
			methodMap.put(methodName, m);
		}

		for (Method m : this.entityClass.getMethods()) {
			String methodName = m.getName();
			Column ca = m.getAnnotation(Column.class);
			if (methodName.startsWith("get")
					&& m.getParameterTypes().length == 0 && ca != null
					&& ca.length() > 0 && m.getReturnType() == String.class) {
				// 将所有String类型、有Column定义，Column中有长度定义 的getter都找出来

				StringGetterEn en = new StringGetterEn(m, ca.length());

				char[] ary = methodName.toCharArray();
				ary[0] = 's';
				String setterName = new String(ary);
				Method setter = methodMap.get(setterName);
				if (setter != null) {
					en.setter = setter;
					getterList.add(en);
				}
			}
		}

		this.fetchSql = String.format("from %s p %s",
				this.entityClass.getSimpleName(), this.getLeftJoinSql("p"));
		if (this.entityClass.getAnnotation(AQCache.class) != null) {
			this.isUseCache = this.entityClass.getAnnotation(AQCache.class)
					.isQCache();
		} else {
			this.isUseCache = false;
		}
	}

	@Autowired
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * 通用查询方法
	 * 
	 * @param sql
	 * @param start
	 *            分页开始节点
	 * @param count
	 *            分页size
	 * @param map
	 *            名称匹配参数
	 * @param values
	 *            数组下标匹配参数
	 * @return
	 */
	Query createQuery(String sql, int start, int count,
			Map<String, Object> map, Object... values) {
		if (StringUtils.isEmpty(sql)) {
			sql = "from " + entityClass.getSimpleName();
		}
		Query query = getSession().createQuery(sql).setCacheable(
				this.isUseCache);
		if (start > 0) {
			query.setFirstResult(start);
		}
		if (count > 0) {
			query.setMaxResults(count);
		}
		if (map != null && !map.isEmpty()) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		} else if (values != null && values.length > 0) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}

	/**
	 * Hibernate4 QBC 查询
	 * 
	 * @param dcriteria
	 *            DetachedCriteria类型对象，用于组织查询语句
	 * @param start
	 *            分页开始节点
	 * @param count
	 *            分页size
	 * @return
	 */
	Criteria createCriteria(DetachedCriteria dcriteria, int start, int count) {
		if (dcriteria == null) {
			dcriteria = DetachedCriteria.forClass(entityClass);
		}
		Criteria criteria = dcriteria.getExecutableCriteria(getSession());
		if (start > 0) {
			criteria.setFirstResult(start);
		}
		if (count > 0) {
			criteria.setMaxResults(count);
		}
		return criteria;
	}

	/**
	 * 查询
	 * 
	 * @param sql
	 * @return
	 */
	public List<E> find(String sql) {
		return find(sql, null);
	}

	/**
	 * 查询--单参数
	 * 
	 * @param sql
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<E> find(String sql, Object value) {
		return createQuery(sql, 0, -1, null, value).list();
	}

	/**
	 * 查询--多参数
	 * 
	 * @param sql
	 * @param values
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<E> find(String sql, Object[] values) {
		return createQuery(sql, 0, -1, null, values).list();
	}

	/**
	 * 分页查询
	 * 
	 * @param sql
	 * @param params
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageQueryResult<E> findInPage(final String sql, Object[] params,
			int pageNo, int pageSize) {
		List<E> items = createQuery(sql, pageNo * pageSize + 1, pageSize, null,
				params).list();
		int total = this.getCount("select count(*) " + sql, params);
		return new PageQueryResult<>(total, items, pageNo, pageSize);
	}

	/**
	 * 分页查询--带外键的“left join”连接查询
	 * 
	 * @param sqlStartWithWhere
	 * @param tableShortName
	 * @param form
	 *            当前翻页的form对象
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageQueryResult<E> findInPageFetch(final String sqlStartWithWhere,
			String tableShortName, IPageForm form, Object[] params) {
		StringBuffer sql = new StringBuffer(100);
		sql.append("from ").append(this.entityClass.getSimpleName())
				.append(' ').append(tableShortName);

		sql.append(this.getLeftJoinSql(tableShortName));

		sql.append(' ');

		sql.append(sqlStartWithWhere);

		List<E> items = createQuery(sql.toString(),
				form.getPageNo() * form.getPageSize() + 1, form.getPageSize(),
				null, params).list();
		int total = this.getCount(String.format(
				"select count(*) from %s %s %s",
				this.entityClass.getSimpleName(), tableShortName,
				sqlStartWithWhere), params);
		return new PageQueryResult<>(total, items, form.getPageNo(),
				form.getPageSize());
	}

	/**
	 * 根据主键获取对象数据，不包含多对一关联表的数据
	 * 
	 * @param id
	 * @return
	 */
	public E get(PK id) {
		return getSession().get(entityClass, id);
	}

	/**
	 * 查询全部（有外键会进行关联查询）
	 * 
	 * @return
	 */
	public List<E> getAll() {
		if (this.foreignKeyList.isEmpty()) {
			return this.getAllNoFetch();
		} else {
			return find(fetchSql);
		}
	}

	/**
	 * 查询全部--无论有无外键都不进行关联查询
	 * 
	 * @return
	 */
	public List<E> getAllNoFetch() {
		return find(null);
	}

	/**
	 * 根据主键获取对象数据--(支持连接查询，并且支持复合主键的链接查询)
	 * 
	 * @param id
	 * @return
	 */
	public E getAndFetch(PK id) {
		if (this.foreignKeyList.isEmpty()) {
			// 如果是没有外键的
			return this.get(id);
		} else {
			if (this.embeddableId) {
				// 复合类型主键的
				return this.getEmbedId(id);
			} else {
				// 普通主键的
				return this.getNormalId(id);
			}
		}
	}

	/**
	 * 构建ID查询的关联查询语句
	 * 
	 * @param id
	 * @param params
	 * @return
	 */
	private String buildGetSql(PK id, List<Object> params) {
		StringBuffer sql = new StringBuffer(200);
		sql.append("from ").append(this.entityClass.getSimpleName())
				.append(" p");
		sql.append(this.getLeftJoinSql("p"));
		sql.append(" where");

		boolean needAddAnd = false;
		for (Field f : this.entityClass.getFields()) {
			if (f.getAnnotation(Column.class) == null) {
				continue;
			}

			String fieldName = f.getName();
			if (fieldName != null) {
				if (!needAddAnd) {
					needAddAnd = true;
				} else {
					sql.append(" and");
				}

				sql.append(String.format(" p.id.%s=?", fieldName));
				try {
					Method m = entityClass.getMethod(
							"get" + StringUtils.capitalize(fieldName),
							new Class[0]);
					params.add(m.invoke(id));
				} catch (Exception e) {
					log.error("buildGetSql时，发生错误", e);
				}
			}
		}

		return sql.toString();
	}

	/**
	 * 普通ID级联查询
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private E getNormalId(final PK id) {
		Query q = getSession().createQuery(
				String.format("%s where p.%s=?", fetchSql, idName));
		q.setParameter(0, id);
		Object res = q.uniqueResult();
		return (E) res;
	}

	/**
	 * 根据复合主键级联查询
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private E getEmbedId(PK id) {
		final List<Object> params = new LinkedList<>();
		final String sql = buildGetSql(id, params);

		Query q = getSession().createQuery(sql);
		int index = 0;
		for (Object obj : params) {
			q.setParameter(index, obj);
			index++;
		}
		Object res = q.uniqueResult();

		return (E) res;
	}

	/**
	 * 生成left join fetch 的sql字段
	 * 
	 * @param tableShortName
	 * @return
	 */
	protected String getLeftJoinSql(String tableShortName) {
		if (this.foreignKeyList.isEmpty()) {
			return "";
		} else {
			StringBuffer sql = new StringBuffer(100);
			for (String str : this.foreignKeyList) {
				sql.append(String.format(" left join fetch %s.%s",
						tableShortName, str));
			}
			return sql.toString();
		}
	}

	/**
	 * 获取数据总量
	 * 
	 * @param sql
	 * @param values
	 * @return
	 */
	public int getCount(String sql, Object[] values) {
		List<?> list = find(sql, values);
		if (list.size() > 0) {
			Long o = (Long) list.get(0);
			return o.intValue();
		}
		return 0;
	}

	/**
	 * 将sql结果级返回的第一个字段以double的方式返回，通常用于 select sum(*) 这样的语句
	 */
	public double getSum(String hql, Object value) {
		double sum = 0d;
		List<?> list = find(hql, value);
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
	}

	/**
	 * 根据页码和每页记录数执行查询
	 * 
	 * @param hql
	 * @param param
	 *            参数
	 * @param pageNo
	 *            页码从1开始
	 * @param pageSize
	 *            每页显示的记录数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<E> pageQuery(String hql, Object param, int pageNo, int pageSize) {
		List<E> list = createQuery(hql, pageNo * pageSize + 1, pageSize, null,
				param).list();
		return list;
	}

	/**
	 * 根据页码和每页记录数执行查询
	 * 
	 * @param hsql
	 * @param params
	 *            参数
	 * @param pageNo
	 *            页码从1开始
	 * @param pageSize
	 *            每页显示的记录数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<E> pageQuery(final String hsql, final Object[] params,
			final int pageNo, final int pageSize) {
		final int _pageNo;
		if (pageNo < 1) {
			_pageNo = 0;
		} else {
			_pageNo = pageNo - 1;
		}
		final int _pageSize;
		if (pageSize < 1) {
			_pageSize = 5;
		} else {
			_pageSize = pageSize;
		}
		List<E> list = createQuery(hsql, _pageNo * _pageSize + 1, _pageSize,
				null, params).list();
		return list;
	}

	/**
	 * QBC查询，带分页
	 * 
	 * @param dcriteria
	 *            DetachedCriteria.forClass(Entity.class)获取实例
	 * @param start
	 *            分页开始节点
	 * @param count
	 *            分页size
	 * @return
	 */
	public List<?> queryByCriteria(DetachedCriteria dcriteria, int start,
			int count) {
		List<?> list = createCriteria(dcriteria, start, count).list();
		return list;
	}

	/**
	 * QBC查询，无分页
	 * 
	 * @param dcriteria
	 * @return
	 */
	public List<?> queryByCriteria(DetachedCriteria dcriteria) {
		return queryByCriteria(dcriteria, 0, -1);
	}

	/**
	 * 删除一条记录
	 * 
	 * @param po
	 */
	public void remove(E po) {
		this.getSession().delete(po);
	}

	/**
	 * 根据主键删除
	 * 
	 * @param id
	 */
	public void removeByID(PK id) {
		E po = this.get(id);
		if (po != null) {
			this.remove(po);
		}
	}

	/**
	 * 更新or保存
	 * 
	 * @param pojo
	 */
	public void save(E pojo) {
		try {
			for (StringGetterEn en : this.getterList) {

				String value = (String) en.getter.invoke(pojo);
				if (value != null && value.length() > en.length) {
					String newvalue = com.cfido.commons.utils.utils.StringUtils
							.substring(value, 0, en.length);
					en.setter.invoke(pojo, newvalue);

					log.warn(LogUtil.format(
							"存储对象 %s 时，字段%s的值的长度过长，自动将长度截取到%d, 新的值为:%s",
							this.entityClass.getSimpleName(),
							com.cfido.commons.utils.utils.StringUtils.substring(en.getter.getName(), 3, 100),
							en.length,
							newvalue));
				}
			}
		} catch (Exception e) {
			log.error("保存对象时，发生错误", e);
		}
		if (pojo != null) {
			try {
				getSession().saveOrUpdate(pojo);
			} catch (Exception e) {
				log.error("保存对象时，发生错误", e);
			}
		}
	}

	/**
	 * 刷新对象
	 * 
	 * @param pojo
	 */
	public void refresh(E pojo) {
		if (pojo != null) {
			getSession().refresh(pojo);
		}
	}

}
