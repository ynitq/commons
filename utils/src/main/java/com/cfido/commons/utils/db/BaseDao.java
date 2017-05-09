package com.cfido.commons.utils.db;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.util.StringUtils;

import com.cfido.commons.beans.form.IPageForm;
import com.cfido.commons.utils.cache.RedisGo;
import com.cfido.commons.utils.utils.ClassUtil;
import com.cfido.commons.utils.utils.LogUtil;
import com.cfido.commons.utils.web.PageForm;

/**
 * <pre>
 * Dao的基类，集成了常用的各类数据库操作方法
 * 
 * 为了可以适用于多个数据源，本方法的getHibernateTemplate()方法必须是abstract的
 * </pre>
 * 
 * @author 梁韦江 2015年7月18日
 * @param <T>
 *            po的类型
 * @param <K>
 *            po的ID的类型
 */
public abstract class BaseDao<T, K extends Serializable> implements IObjFactoryDao<T, K> {

	/**
	 * <pre>
	 * 用于存储表中和各个字段的长度，防止在保存的时候数据的长度过长
	 * </pre>
	 * 
	 * @author 梁韦江 2015年7月18日
	 */
	class StringGetterEn {
		private final Method getter;
		private final int length;
		private Method setter;

		private StringGetterEn(Method getter, int length) {
			super();
			this.getter = getter;
			this.length = length;
		}

		@Override
		public String toString() {
			return String.format("setter:%s getter:%s 长度:%d", this.setter.getName(), this.getter.getName(), this.length);
		}
	}

	/** 有外键的时候，放在将所有关联字段全部取出的sql */
	private final String fetchSql;

	/** id是否是复合类型 */
	protected final boolean embeddableId;

	/** 从泛型中找到的po的类型 */
	protected final Class<T> entityClass;

	/** 存储所有的外键 */
	protected final List<String> foreignKeyList = new LinkedList<>();

	/** 存储所有有长度现在的字段 */
	protected final List<StringGetterEn> getterList = new LinkedList<>();

	/** id的类型 */
	protected final Class<K> idClass;

	/** id的名字 */
	protected String idName;

	protected final Log log = LogFactory.getLog(getClass());

	@SuppressWarnings({
			"unchecked"
	})
	public BaseDao() {
		this.entityClass = (Class<T>) ClassUtil.getGenericType(this.getClass(), 0);
		this.idClass = (Class<K>) ClassUtil.getGenericType(this.getClass(), 1);

		this.embeddableId = idClass.getAnnotation(Embeddable.class) != null;

		Map<String, Method> methodMap = new HashMap<>();
		for (Method m : this.entityClass.getMethods()) {
			String methodName = m.getName();
			methodMap.put(methodName, m);
			if (methodName.startsWith("get") && methodName.length() > 3) {

				if (m.getAnnotation(Id.class) != null) {
					this.idName = StringUtils.uncapitalize(methodName.substring(3));
				}

				if (m.getAnnotation(ManyToOne.class) != null) {
					this.foreignKeyList.add(methodName.substring(3));

				}
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

		for (Method m : this.entityClass.getMethods()) {
			String methodName = m.getName();
			Column ca = m.getAnnotation(Column.class);
			if (methodName.startsWith("get") && m.getParameterTypes().length == 0 && ca != null && ca.length() > 0
					&& m.getReturnType() == String.class) {
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

		this.fetchSql = String.format("from %s p %s", this.entityClass.getSimpleName(), this.getLeftJoinSql("p"));
	}

	@Override
	public List<T> findAll() {
		String sql = "from " + this.entityClass.getSimpleName();
		return this.find(sql);
	}

	public List<T> find(String hsql) {
		return this.findForClass(hsql, null, this.entityClass, null);
	}

	public List<T> find(String hsql, Object value) {
		Object[] values = new Object[] {
				value
		};
		return this.findForClass(hsql, null, this.entityClass, values);
	}

	@Override
	public List<T> find(String hsql, Object... values) {
		return this.findForClass(hsql, null, this.entityClass, values);
	}

	/**
	 * 可用于返回任何类型List的查询
	 * 
	 * @param hsql
	 *            查询语句
	 * @param pageForm
	 *            包含页码和每页长度的form，该参数为空时，表示不分页，查询所有
	 * @param clazz
	 *            返回的List中的类型
	 * @param params
	 *            其他参数
	 * @return
	 */
	public <L> List<L> findForClass(final String hsql, final IPageForm pageForm, Class<L> clazz, final Object[] params) {
		long start = System.currentTimeMillis();
		if (log.isDebugEnabled()) {
			log.debug(LogUtil.printSql(hsql, params));
		}
		List<L> result = this.getHibernateTemplate().execute(new HibernateCallback<List<L>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<L> doInHibernate(Session session) throws HibernateException {
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

				return q.list();
			}
		});

		long end = System.currentTimeMillis();

		// 如果sql执行超过1秒，则发出警告，提示程序猿检查SQL代码
		if (end - start > 1000) {
			log.debug("########当前SQL执行时间超出1秒 : " + hsql + "########");
		}

		return result;
	}

	/**
	 * @param csql
	 * @param params
	 * @return
	 * @author 刘罡
	 */
	public int getCountBySql(final String csql, final Object[] params) {
		BigInteger ret = this.getHibernateTemplate().execute(new HibernateCallback<BigInteger>() {
			@Override
			public BigInteger doInHibernate(Session session) throws HibernateException {
				SQLQuery sqlQuery = session.createSQLQuery(csql);
				for (int i = 0, len = params.length; i < len; i++) {
					sqlQuery.setParameter(i, params[i]);
				}
				Object result = sqlQuery.uniqueResult();
				return (BigInteger) result;
			}
		});
		return ret.intValue();
	}

	/**
	 * @param sql
	 * @param pageForm
	 * @param clazz
	 * @param params
	 * @return
	 * @author 刘罡
	 */
	public <L> List<L> findForClassBySql(final String sql, final IPageForm pageForm, final Class<L> clazz,
			final Object[] params) {
		if (log.isDebugEnabled()) {
			log.debug(LogUtil.printSql(sql, params));
		}
		List<L> result = this.getHibernateTemplate().execute(new HibernateCallback<List<L>>() {
			@SuppressWarnings("unchecked")
			@Override
			public List<L> doInHibernate(Session session) throws HibernateException {
				Query q = session.createSQLQuery(sql);
				setQueryParameters(q, params);
				FixedPagingSpec pagingSpec = fixPaging(pageForm);
				if (pagingSpec != null) {
					q.setFirstResult((pagingSpec.getPageNo()) * pagingSpec.getPageSize());
					q.setMaxResults(pagingSpec.getPageSize());
				}
				q.setResultTransformer(new AliasToBeanResultTransformer(clazz));
				return q.list();
			}
		});
		return result;
	}

	/**
	 * @param sql
	 * @param csql
	 * @param voClass
	 * @param params
	 * @param form
	 * @return
	 * @author 刘罡
	 */
	public <L> PageQueryResult<L> findInPageForClassBySql(String sql, String csql, Class<L> voClass, Object[] params,
			IPageForm form) {
		List<L> items = this.findForClassBySql(sql, form, voClass, params);
		int total = this.getCountBySql(csql, params);
		return new PageQueryResult<>(total, items, form);
	}

	/**
	 * @param q
	 * @param params
	 * @author 刘罡
	 */
	private void setQueryParameters(Query q, Object[] params) {
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				q.setParameter(i, params[i]);
			}

		}
	}

	private FixedPagingSpec fixPaging(IPageForm pageForm) {
		if (pageForm == null) {
			return null;
		}
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
		return new FixedPagingSpec(pageNo, pageSize);
	}

	class FixedPagingSpec {
		final private int pageNo;
		final private int pageSize;

		FixedPagingSpec(int pageNo, int pageSize) {
			this.pageNo = pageNo;
			this.pageSize = pageSize;
		}

		public int getPageNo() {
			return pageNo;
		}

		public int getPageSize() {
			return pageSize;
		}
	}

	public PageQueryResult<T> findInPage(final String fromSql, Object[] params, IPageForm form) {
		String countSql = "select count(*) " + fromSql;

		return this.findInPageForClass(fromSql, countSql, this.entityClass, params, form);
	}

	@Override
	public PageQueryResult<T> findInPage(String fromSql, IPageForm form, Object... params) {
		String countSql = "select count(*) " + fromSql;
		return this.findInPageForClass(fromSql, countSql, this.entityClass, params, form);
	}

	/**
	 * 分页查询，但是查询出来的PO会封装在VoForPo的之类中，而不是po本身
	 * 
	 * @param voClazz
	 *            vo的类型，该类型必须继承于VoForPo，而且必须有无参数的构造函数
	 * @param fromSql
	 *            以from开头的HSQL, 注意，不是select开头的
	 * @param params
	 *            sql需要用到的参数
	 * @param form
	 *            IPageForm接口的表单
	 * @return
	 * 
	 * @see VoForPo VO的基类
	 */
	public <L extends VoForPo<T>> PageQueryResult<L> findInPageForVo(Class<L> voClazz, String fromSql, Object[] params,
			IPageForm form) {
		String countSql = "select count(*) " + fromSql;

		PageQueryResult<T> temp = this.findInPageForClass(fromSql, countSql, this.entityClass, params, form);

		List<L> newList = new LinkedList<>();
		for (T po : temp.getList()) {
			try {
				L vo = voClazz.newInstance();
				vo.setPo(po);
				newList.add(vo);
			} catch (Exception e) {
				LogUtil.traceError(log, e);
			}
		}

		PageQueryResult<L> res = new PageQueryResult<>(temp.getItemTotal(), newList, form);
		return res;
	}

	public PageQueryResult<T> findInPage(final String sql, Object[] params, int pageNo, int pageSize) {
		PageForm form = new PageForm();
		form.setPageNo(pageNo);
		form.setPageSize(pageSize);

		return this.findInPage(sql, params, form);
	}

	/**
	 * 可根据输入的vo的类型，执行分页查询
	 * 
	 * @param sql
	 *            要执行的sql
	 * @param countHsql
	 *            计算总数的sql
	 * @param voClass
	 *            vo的class
	 * @param params
	 *            参数数组
	 * @param form
	 *            分页表单
	 * @return
	 */
	public <L> PageQueryResult<L> findInPageForClass(String sql, String countHsql, Class<L> voClass, Object[] params,
			IPageForm form) {
		List<L> items = this.findForClass(sql, form, voClass, params);
		int total = this.getCount(countHsql, params);
		return new PageQueryResult<>(total, items, form);
	}

	/**
	 * 对有多对一关系的表进行级联查询，fatch所有关联的表，并加查询结果放到页面结果类中
	 * 
	 * <pre>
	 * 要求输入拼接后的条件sql，例如 where p.createTime=?
	 * 方法会自动添加 from User p 在前面
	 * 如果有多对一关系，会添加成为 from User p left join fetch p.Rights 字样
	 * 所以传入的sql只能是where以后的部分， 也要将“p” 这个在sql中用到的别名传进来
	 * </pre>
	 * 
	 * @param sqlStartWithWhere
	 *            没有select和from where开始的那部分
	 * @param tableShortName
	 *            表的别名，例如 p
	 * @param form
	 *            含有pageNo pageSize方法的表单
	 * @param params
	 *            参数
	 * @return
	 */
	public PageQueryResult<T> findInPageFetch(final String sqlStartWithWhere, String tableShortName, IPageForm form,
			Object[] params) {
		StringBuffer sql = new StringBuffer(100);

		sql.append("from ").append(this.entityClass.getSimpleName()).append(' ').append(tableShortName);
		sql.append(this.getLeftJoinSql(tableShortName));
		sql.append(' ');
		sql.append(sqlStartWithWhere);

		List<T> items = this.findForClass(sql.toString(), form, this.entityClass, params);
		int total = this.getCount(String.format("select count(*) from %s %s %s", this.entityClass.getSimpleName(),
				tableShortName, sqlStartWithWhere), params);
		return new PageQueryResult<>(total, items, form.getPageNo(), form.getPageSize());
	}

	/**
	 * 根据主键获得记录，但不包含多对一关联表的数据
	 */
	@Override
	public T get(K id) {
		return this.getHibernateTemplate().get(entityClass, id);
	}

	/**
	 * 获得表中的所有数据，但包含多对一关联表的数据
	 * 
	 * @return
	 */
	public List<T> getAll() {
		if (this.foreignKeyList.isEmpty()) {
			return this.getAllNoFetch();
		} else {
			return this.findForClass(fetchSql, null, this.entityClass, null);
		}
	}

	/**
	 * 获得表中的所有数据，但不包含多对一关联表的数据
	 * 
	 * @return
	 */
	public List<T> getAllNoFetch() {
		return this.getHibernateTemplate().loadAll(entityClass);
	}

	public T getAndFetch(K id) {
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
	 * 将sql结果级返回的第一个字段以整数的方式返回，通常用于 select count(*) 这样的语句
	 * 
	 * @param queryString
	 * @param values
	 * @return
	 */
	public int getCount(String queryString, Object[] values) {
		List<?> list = this.getHibernateTemplate().find(queryString, values);
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
		List<?> list = getHibernateTemplate().find(hql, value);
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
	 * @param form
	 *            翻页表单
	 * @return
	 */
	public List<T> pageQuery(String hql, Object param, IPageForm form) {
		Object[] params = new Object[] {
				param
		};
		return this.pageQuery(hql, params, form);
	}

	/**
	 * 根据页码和每页记录数执行查询
	 * 
	 * @param hsql
	 * @param params
	 *            参数
	 * @param form
	 *            翻页表单
	 * @return
	 */
	public List<T> pageQuery(final String hsql, final Object[] params, IPageForm form) {
		return this.findForClass(hsql, form, this.entityClass, params);
	}

	public List<T> pageQuery(final String hsql, final Object[] params, int pageNo, int pageSize) {
		PageForm form = new PageForm();
		form.setPageNo(pageNo);
		form.setPageSize(pageSize);

		return this.findForClass(hsql, form, this.entityClass, params);
	}

	/**
	 * 刷新记录
	 * 
	 * @param po
	 */
	public void refresh(T po) {
		this.getHibernateTemplate().refresh(po);
	}

	@Override
	public void delete(T po) {
		this.remove(po);
	}

	/**
	 * 删除一条记录
	 * 
	 * @param po
	 */
	public void remove(T po) {
		try {
			// 如果开启了缓存，那么我们在这个方法就执行removeCache操作
			log.debug(String.format("缓存开启状态：", RedisGo.go));
			if (RedisGo.go) {
				removeCache(po);
			}
		} catch (Exception e) {
			LogUtil.traceError(log, e);
		}
		this.getHibernateTemplate().delete(po);
	}

	/**
	 * 根据主键删除
	 * 
	 * @param id
	 */
	public void removeByID(K id) {
		T po = this.get(id);
		if (po != null) {
			this.remove(po);
		}
	}

	/**
	 * 删除缓存
	 * 
	 * @param po
	 * @param isDel
	 */
	public void removeCache(T po, boolean isDel) {
		if (this.redisTemplate == null) {
			return;
		}
		try {
			log.debug(String.format("===========delete cache：", po.getClass().getSimpleName()));
			String poName = com.cfido.commons.utils.utils.StringUtilsEx.toUpperCamelCase(po.getClass().getSimpleName(), false);
			Method[] ms = po.getClass().getMethods();
			for (Method m : ms) {
				if (m.getName().equals("getId")) {
					Integer idVal = (Integer) m.invoke(po);
					String key = String.format("%sId_%d", poName, idVal);

					// 先删除当前缓存
					redisTemplate.delete(key);

					// 缓存同步相关
					Long size = redisTemplate.boundZSetOps(String.format("%sList~keys", poName)).zCard();

					// 由于初始配置里面老梁配的是Json的序列化，所以这里很坑爹的要用String反序列话，所以先设置成String，再设置回去。。。
					RedisSerializer<?> serializer = redisTemplate.getValueSerializer();
					redisTemplate.setValueSerializer(redisTemplate.getStringSerializer());
					Set<Object> keys = redisTemplate.opsForZSet().range(String.format("%sList~keys", poName), 0, size);
					redisTemplate.setValueSerializer(serializer);
					for (Object k : keys) {
						// 删除所有对象相关的查询--暂时不做部分删除了，调着蛋疼
						redisTemplate.delete(k.toString());
					}
					log.debug(String.format("--成功删除缓存（key为：%s）", key));
				}
			}
		} catch (Exception ex) {
			LogUtil.traceError(log, ex, "删除缓存时出错了");
		}
	}

	/**
	 * 删除缓存
	 * 
	 * @param po
	 */
	public void removeCache(T po) {
		removeCache(po, false);
	}

	@Autowired(required = false)
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * 保存或者更新
	 * 
	 * @param po
	 */
	public void save(T po) {
		try {
			// 如果开启了缓存，那么我们在这个方法就执行removeCache操作
			log.debug(String.format("缓存开启状态：", RedisGo.go));
			if (RedisGo.go) {
				removeCache(po);
			}
			for (StringGetterEn en : this.getterList) {

				String value = (String) en.getter.invoke(po);
				if (value != null && value.length() > en.length) {
					String newvalue = com.cfido.commons.utils.utils.StringUtilsEx.substring(value, 0, en.length);
					en.setter.invoke(po, newvalue);

					log.warn(LogUtil.format("存储对象 %s 时，字段%s的值的长度过长，自动将长度截取到%d, 新的值为:%s", this.entityClass.getSimpleName(),
							com.cfido.commons.utils.utils.StringUtilsEx.substring(en.getter.getName(), 3, 100), en.length,
							newvalue));
				}
			}
		} catch (Exception e) {
			LogUtil.traceError(log, e);
		}
		if (this.getHibernateTemplate() != null) {
			this.getHibernateTemplate().saveOrUpdate(po);
		}
	}

	private String buildGetSql(K id, List<Object> params) {
		StringBuffer sql = new StringBuffer(200);
		sql.append("from ").append(this.entityClass.getSimpleName()).append(" p");
		sql.append(this.getLeftJoinSql("p"));
		sql.append(" where");

		boolean needAddAnd = false;
		for (Method m : this.idClass.getMethods()) {
			if (m.getAnnotation(Column.class) == null) {
				continue;
			}

			String methodName = m.getName();
			String prop = null;
			if (methodName.startsWith("get") && methodName.length() > 3) {
				prop = StringUtils.uncapitalize(methodName.substring(3));
			} else if (methodName.startsWith("is") && methodName.length() > 2) {
				prop = StringUtils.uncapitalize(methodName.substring(2));
			}
			if (prop != null) {
				if (!needAddAnd) {
					needAddAnd = true;
				} else {
					sql.append(" and");
				}

				sql.append(String.format(" p.id.%s=?", prop));
				try {
					params.add(m.invoke(id));
				} catch (Exception e) {
					LogUtil.traceError(log, e);
				}
			}
		}

		return sql.toString();
	}

	private T getEmbedId(K id) {
		final List<Object> params = new LinkedList<>();
		final String sql = buildGetSql(id, params);

		T po = this.getHibernateTemplate().execute(new HibernateCallback<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T doInHibernate(Session session) throws HibernateException {
				Query q = session.createQuery(sql);
				int index = 0;
				for (Object obj : params) {
					q.setParameter(index, obj);
					index++;
				}
				Object res = q.uniqueResult();

				return (T) res;
			}
		});
		return po;
	}

	/**
	 * 普通ID的级联查询
	 * 
	 * @param id
	 * @return
	 */
	private T getNormalId(final K id) {
		T po = this.getHibernateTemplate().execute(new HibernateCallback<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T doInHibernate(Session session) throws HibernateException {

				Query q = session.createQuery(String.format("%s where p.%s=?", fetchSql, idName));
				q.setParameter(0, id);

				Object res = q.uniqueResult();

				return (T) res;
			}
		});
		return po;
	}

	protected abstract HibernateTemplate getHibernateTemplate();

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
				sql.append(String.format(" left join fetch %s.%s", tableShortName,
						StringUtils.uncapitalize(str)));
			}
			return sql.toString();
		}
	}

	public T get(K id, LockMode lockModel) {
		return this.getHibernateTemplate().get(entityClass, id, lockModel);
	}

	@Override
	public void insert(T po) {
		this.save(po);
	}

	@Override
	public void update(T po, boolean cleanListCache) {
		this.save(po);
		if (cleanListCache) {
			removeCache(po);
		}
	}

	@Override
	public String getIdFieldName() {
		return this.idName;
	}

	@Override
	public Class<T> getEntityClass() {
		return this.entityClass;
	}

}
