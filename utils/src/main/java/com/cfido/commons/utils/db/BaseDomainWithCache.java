package com.cfido.commons.utils.db;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.cfido.commons.beans.exceptions.DaoException;
import com.cfido.commons.beans.form.IPageForm;
import com.cfido.commons.utils.utils.ClassUtil;
import com.cfido.commons.utils.utils.EncryptUtil;
import com.cfido.commons.utils.utils.SqlUtils;

/**
 * <pre>
 * 使用了多级Cache技术的Domain基类，类似以前的service,但都是对某个表操作的
 * </pre>
 * 
 * @author 梁韦江 2016年8月18日
 */
public abstract class BaseDomainWithCache<PO, ID extends Serializable> implements IObjFactoryDao<PO, ID> {

	private class BuildCacheKeyForListResult {
		private String fullsql;
		private String key;
		private String pageStr;

		private String getKey() {
			if (cacheManager == null) {
				return "";
			}
			if (key == null) {
				String md5 = EncryptUtil.md5(fullsql + "\t" + pageStr);
				key = BaseDomainWithCache.this.cacheOfList.getName() + ":" + md5;
			}
			return key;
		}
	}

	@Autowired
	private CacheManager cacheManager;

	private Cache cacheOfList;

	private Cache cacheOfPo;

	private SafeSave<PO> saveSave;

	protected final String idFieldName;

	/** 从泛型中找到的po的类型 */
	protected final Class<PO> entityClass;

	/** 从泛型中找到的po的类型 */
	protected final Class<ID> idClass;

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BaseDomainWithCache.class);

	/** 可获得id值的方法 */
	protected final Method methodOfGetId;

	public BaseDomainWithCache() {
		this(null);
	}

	@SuppressWarnings("unchecked")
	public BaseDomainWithCache(Class<ID> idClass) {
		this.entityClass = (Class<PO>) ClassUtil.getGenericType(this.getClass(), 0);
		this.idClass = this.buildIdClass(idClass);
		this.methodOfGetId = ClassUtil.getIdMethod(entityClass);

		Assert.notNull(this.entityClass, "应该可找到PO的Class");
		Assert.notNull(this.idClass, "应该可找到ID的Class");
		Assert.notNull(this.methodOfGetId, "应该可找到getId的Method");

		this.saveSave = new SafeSave<>(this.entityClass);
		this.idFieldName = this.methodOfGetId.getName().substring(3).toLowerCase();
	}

	@SuppressWarnings("unchecked")
	private Class<ID> buildIdClass(Class<ID> idClass) {
		if (idClass != null) {
			return idClass;
		} else {
			return (Class<ID>) ClassUtil.getGenericType(this.getClass(), 1);
		}
	}

	/**
	 * 删除po
	 * 
	 * @param po
	 * @throws DaoException
	 */
	@Override
	@Transactional
	public void delete(PO po) throws DaoException {
		Assert.notNull(po, "po 不能为空");

		this.getCommonDao().delete(po);

		// 从cache中删除对象
		this.poCacheDelete(po);
	}

	/**
	 * 获得所有
	 * 
	 * @return
	 * @throws DaoException
	 */
	@Override
	public List<PO> findAll() throws DaoException {
		String sql = String.format("from %s order by %s desc",
				this.entityClass.getSimpleName(),
				this.idFieldName);
		return this.findPo(sql, null);
	}

	/**
	 * 可根据输入的vo的类型，执行分页查询
	 * 
	 * @param sqlStartWithFrom
	 *            要执行的sql
	 * @param pageForm
	 *            分页表单
	 * @param params
	 *            参数数组
	 * @return
	 * @throws DaoException
	 */
	@Override
	public PageQueryResult<PO> findInPage(String sqlStartWithFrom, IPageForm pageForm, Object... params) throws DaoException {

		List<PO> items = this.findPo(sqlStartWithFrom, this.idFieldName, pageForm, params);

		String countHsql = "select count(*) " + sqlStartWithFrom;
		int total = this.getCommonDao().getCount(countHsql, params);
		return new PageQueryResult<>(total, items, pageForm);
	}

	/**
	 * 可根据输入的vo的类型，执行分页查询
	 * 
	 * @param sqlStartWithFrom
	 *            要执行的sql
	 * @param idField
	 *            id字段, 例如a.id
	 * @param pageForm
	 *            分页表单
	 * @param params
	 *            参数数组
	 * @return
	 * @throws DaoException
	 */
	public PageQueryResult<PO> findInPage(String sqlStartWithFrom, String idField, IPageForm pageForm, Object... params)
			throws DaoException {
		Assert.notNull(pageForm, "分页表单pageForm不能为空");

		List<PO> items = this.findPo(sqlStartWithFrom, idField, pageForm, params);

		String countHsql = "select count(*) " + sqlStartWithFrom;
		int total = this.getCommonDao().getCount(countHsql, params);
		return new PageQueryResult<>(total, items, pageForm);
	}

	/**
	 * 根据条件，寻找一个对象
	 * 
	 * @param sqlStartWithFrom
	 * @param params
	 * @return
	 * @throws DaoException
	 */
	public PO findOne(String sqlStartWithFrom, Object... params) throws DaoException {
		List<PO> list = this.findPo(sqlStartWithFrom, idFieldName, IPageForm.ONE, params);
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}

	}

	@Override
	public List<PO> find(String sqlStartWithFrom, Object... params) {
		List<PO> list = this.findPo(sqlStartWithFrom, idFieldName, null, params);
		return list;
	}

	/**
	 * 通 from开头的sql语句搜索
	 * 
	 * @param sqlStartWithFrom
	 * @param pageForm
	 * @param params
	 * @return
	 * @throws DaoException
	 */
	public List<PO> findPo(String sqlStartWithFrom, IPageForm pageForm, Object... params) throws DaoException {
		List<ID> list = this.findId(sqlStartWithFrom, this.idFieldName, pageForm, params);
		return this.idListToPoList(list);
	}

	/**
	 * 通 from开头的sql语句搜索
	 * 
	 * @param sqlStartWithFrom
	 * @param idField
	 * @param pageForm
	 * @param params
	 * @return
	 * @throws DaoException
	 */
	public List<PO> findPo(String sqlStartWithFrom, String idField, IPageForm pageForm, Object... params) throws DaoException {
		List<ID> list = this.findId(sqlStartWithFrom, idField, pageForm, params);
		return this.idListToPoList(list);
	}

	/**
	 * 获得po
	 * 
	 * @param id
	 * @return
	 * @throws DaoException
	 */
	@Override
	public PO get(ID id) throws DaoException {
		Assert.notNull(id, "id不能为空");

		PO po = this.poCachePoGet(id);
		if (po == null) {
			log.debug("重数据库 {} 中获取数据  id={}", this.entityClass.getSimpleName(), id);
			po = this.getCommonDao().get(this.entityClass, id);
			if (po != null) {
				this.poCachePut(po);
			}
		}
		return po;
	}

	@Override
	public Class<PO> getEntityClass() {
		return entityClass;
	}

	/**
	 * insert到数据库
	 * 
	 * @param po
	 * @throws DaoException
	 */
	@Override
	@Transactional
	public void insert(PO po) throws DaoException {
		Assert.notNull(po, "po 不能为空");

		this.saveSave.process(po);

		this.getCommonDao().insert(po);

		// 保存时，将对象放到cache中
		this.poCachePut(po);
		// 有一个对象发生了变化，也需要重置所有的list
		this.resetListCache();
	}

	/**
	 * update记录
	 * 
	 * @param po
	 * @throws DaoException
	 */
	@Override
	@Transactional
	public void update(PO po, boolean resetCache) throws DaoException {
		Assert.notNull(po, "po 不能为空");

		this.saveSave.process(po);

		this.getCommonDao().update(po);

		// 保存时，将对象放到cache中
		this.poCachePut(po);

		if (resetCache) {
			// 更新cache时，不一定要重置所有的list
			this.resetListCache();
		}
	}

	/**
	 * 根据sql和条件构建key
	 * 
	 * @param hsql
	 * @param pageForm
	 * @param params
	 * @return
	 */
	private BuildCacheKeyForListResult buildCacheKeyForList(String hsql, IPageForm pageForm, Object... params) {
		BuildCacheKeyForListResult res = new BuildCacheKeyForListResult();

		if (this.isNeedCache()) {
			// 如果有sql设置，才需要生产key
			res.fullsql = SqlUtils.printSql(hsql, params);

			if (pageForm == null) {
				// 所有记录
				res.pageStr = "allPage";
			} else {
				// 有翻页信息
				pageForm.verifyPageNo();
				res.pageStr = String.format("%d/%d", pageForm.getPageNo(), pageForm.getPageSize());
			}
		}

		return res;
	}

	private List<PO> findPoInIds(Collection<ID> idSet) throws DaoException {
		if (idSet.isEmpty()) {
			return new LinkedList<>();
		} else {
			String hsql = String.format("from %s where %s in (?1)", this.entityClass.getSimpleName(), this.idFieldName);
			return this.getCommonDao().find(hsql, null, this.entityClass, idSet);
		}

	}

	/**
	 * 根据id获得cache的key
	 * 
	 * @param id
	 * @return key
	 */
	private String getCacheKeyForID(ID id) {
		String key = String.format("%s:%s", this.cacheOfPo.getName(), String.valueOf(id));
		return key;
	}

	/**
	 * 根据po获得cache的key
	 * 
	 * @param po
	 * @return
	 * @throws DaoException
	 */
	private String getCacheKeyForPO(PO po) {
		ID id = this.getIdFromPO(po);
		return getCacheKeyForID(id);
	}

	/**
	 * 从po对象中获取id的值
	 * 
	 * @param po
	 * @return
	 * @throws DaoException
	 */
	@SuppressWarnings("unchecked")
	private ID getIdFromPO(PO po) {
		try {
			ID id = (ID) this.methodOfGetId.invoke(po);
			Assert.notNull(id, "id 不能为空");
			return id;

		} catch (Exception e) {
			throw new RuntimeException("无法获得 ID " + po, e);
		}
	}

	@SuppressWarnings("unchecked")
	private List<ID> idListCacheGet(String key) {
		if (!this.isNeedCache()) {
			return null;
		}

		if (this.cacheOfList == null) {
			return null;
		}

		CacheValueForList<ID> o = new CacheValueForList<>();
		CacheValueForList<ID> value = this.cacheOfList.get(key, o.getClass());
		if (value != null) {
			return value.getList();
		} else {
			return null;
		}
	}

	private void idListCachePut(BuildCacheKeyForListResult result, List<ID> list) {
		if (!this.isNeedCache()) {
			return;
		}

		if (this.cacheOfList == null || list == null) {
			return;
		}

		CacheValueForList<ID> value = new CacheValueForList<>();
		value.setList(list);
		value.setFullSql(result.fullsql);
		value.setPageStr(result.pageStr);

		this.cacheOfList.put(result.getKey(), value);

		if (log.isDebugEnabled()) {
			log.debug("将List(size={}) 放到 Cache:{} 中，key={}", list.size(),
					this.cacheOfList.getName(),
					result.getKey());
		}
	}

	/**
	 * 是否需要cache
	 * 
	 * @return
	 */
	private boolean isNeedCache() {
		return !this.isCacheDisable() && this.cacheManager != null;
	}

	private void poCacheDelete(PO po) {
		if (!this.isNeedCache()) {
			return;
		}

		if (po == null) {
			return;
		}

		String key = this.getCacheKeyForPO(po);

		this.cacheOfPo.evict(key);

		// 删除po时，要清空list
		this.resetListCache();
	}

	/**
	 * 从cache中获得po
	 * 
	 * @param id
	 * @return
	 */
	private PO poCachePoGet(ID id) {
		if (!this.isNeedCache() || id == null) {
			return null;
		}

		String key = this.getCacheKeyForID(id);
		PO po = this.cacheOfPo.get(key, this.entityClass);
		return po;
	}

	/**
	 * 将对象放到cache中
	 * 
	 * @param po
	 * @throws DaoException
	 */

	private void poCachePut(PO po) {
		if (!this.isNeedCache() || po == null) {
			return;
		}

		String key = this.getCacheKeyForPO(po);
		this.cacheOfPo.put(key, po);

		if (log.isDebugEnabled()) {
			log.debug("将对象{} 放到 Cache:{} , key={}", this.entityClass.getSimpleName(),
					this.cacheOfPo.getName(),
					key);
		}
	}

	/**
	 * 注入完成后，初始cache
	 */
	@PostConstruct
	protected void checkOnInit() {
		if (log.isDebugEnabled()) {
			log.debug("初始化 {} PO={},ID={} Cache={}",
					this.getClass().getSimpleName(),
					this.entityClass.getSimpleName(),
					this.idClass.getSimpleName(),
					this.isNeedCache());
		}

		if (this.isNeedCache()) {
			String nameForPo = String.format("%s_po", this.entityClass.getSimpleName());
			this.cacheOfPo = this.cacheManager.getCache(nameForPo);

			String nameForList = String.format("%s_list", this.entityClass.getSimpleName());
			this.cacheOfList = this.cacheManager.getCache(nameForList);
		}
	}

	protected List<ID> findId(String sqlStartWithFrom, String idField, IPageForm pageForm, Object... params) throws DaoException {
		Assert.notNull(sqlStartWithFrom, "sqlStartWithFrom 不能为空");
		Assert.notNull(idField, "idField 不能为空");
		Assert.isTrue(sqlStartWithFrom.startsWith("from"), "sql 必须以from开头");

		// 构建完整的sql
		String hsql = String.format("select %s %s", idField, sqlStartWithFrom);

		if (this.isNeedCache()) {

			// 根据sql和参数，构建key
			BuildCacheKeyForListResult cacheKey = this.buildCacheKeyForList(sqlStartWithFrom, pageForm, params);

			// 先从cache中寻找
			List<ID> list = this.idListCacheGet(cacheKey.getKey());
			if (list == null) {
				// 如果找不到，就重数据库中找
				list = this.getCommonDao().find(hsql, pageForm, this.idClass, params);

				if (!list.isEmpty()) {
					// 同时放到cache中
					this.idListCachePut(cacheKey, list);
				}
			}

			return list;
		} else {
			// 如果cache没有激活，就从数据库中查询
			return this.getCommonDao().find(hsql, pageForm, this.idClass, params);
		}
	}

	protected abstract ICommonDao getCommonDao();

	/**
	 * 将id的列表转化成为po的列表
	 * 
	 * @param idList
	 * @return
	 * @throws DaoException
	 */
	protected List<PO> idListToPoList(List<ID> idList) throws DaoException {
		if (idList == null) {
			return null;
		}

		List<PO> list;

		if (this.isNeedCache()) {
			Map<ID, PO> map = new HashMap<>();

			// 如果有cache，就先检查哪些id不在cache中
			Set<ID> notInCacheId = new HashSet<>();
			for (ID id : idList) {
				PO po = this.poCachePoGet(id);
				if (po == null) {
					// 如果cache没有，就将ID记录下来，准备批量查询
					notInCacheId.add(id);
				} else {
					// 如果 cache中有，就放到map中
					map.put(id, po);
				}
			}

			// 将那些不再cache中的数据进行批量查询
			if (!notInCacheId.isEmpty()) {
				if (log.isDebugEnabled()) {
					log.debug("查询 {} 时， 需要批量加载数据到cache, ids={}",
							this.entityClass.getSimpleName(), notInCacheId);
				}

				// 如果存在id尚未读取到缓存，就批量读取
				List<PO> poListNotInCache = this.findPoInIds(notInCacheId);
				for (PO po : poListNotInCache) {
					// 将po放到cache中
					this.poCachePut(po);

					// 将po放到map中
					ID id = this.getIdFromPO(po);
					map.put(id, po);
				}
			}

			// 将map中的数据，重新组成list，并返回
			list = new LinkedList<>();
			for (ID id : idList) {
				list.add(map.get(id));
			}

			// 因为有list时，一定有po，所以没必须做批量查询了

		} else {
			// 如果cache被屏蔽了，就直接返回批量查询的结果
			list = this.findPoInIds(idList);
		}

		return list;
	}

	/**
	 * 是否禁止cache
	 * 
	 * @return
	 */
	protected boolean isCacheDisable() {
		return false;
	}

	/**
	 * po发送变更时，要将整个list都清空，因为我们没有办法知道那些查询会发生变化
	 * 
	 */
	void resetListCache() {
		if (this.cacheOfList != null)
			this.cacheOfList.clear();// 清除List的缓存
	}

	@Override
	public String getIdFieldName() {
		return idFieldName;
	}

}
