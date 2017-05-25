package com.cfido.commons.utils.logicObj;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.cfido.commons.beans.apiExceptions.IdNotFoundException;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.form.IPageForm;
import com.cfido.commons.beans.others.IConverter;
import com.cfido.commons.utils.db.IObjFactoryDao;
import com.cfido.commons.utils.db.PageQueryResult;
import com.cfido.commons.utils.db.WhereBuilder;
import com.cfido.commons.utils.utils.ClassUtil;
import com.cfido.commons.utils.utils.ConverterUtil;

/**
 * <pre>
 * 逻辑对象工厂的 基类
 * </pre>
 * 
 * @author 梁韦江 2016年8月30日
 * @param <OBJ>
 *            逻辑对象
 * @param <PO>
 *            原始的数据库对象
 * @param <ID>
 *            id的类型
 */
public abstract class BaseObjFactory<OBJ extends BasePoObj<PO>, PO, ID extends Serializable> {

	/**
	 * po到对象的转换器
	 */
	protected IConverter<PO, OBJ> po2Obj = new IConverter<PO, OBJ>() {

		@Override
		public OBJ convert(PO po) {
			return BaseObjFactory.this.convertToObj(po);
		}

	};

	@Autowired
	private ApplicationContext context;

	private final Class<OBJ> objClass;

	@SuppressWarnings("unchecked")
	public BaseObjFactory() {
		this.objClass = (Class<OBJ>) ClassUtil.getGenericType(getClass(), 0);
		Assert.notNull(objClass, "应该可以找到泛型：" + this.getClass().getName());
	}

	/**
	 * 将数据库对象转换成为逻辑对象
	 * 
	 * @param po
	 * @return
	 */
	public OBJ convertToObj(PO po) {
		if (po == null) {
			return null;
		}

		// 对象必须用scope声明为非单例的，然后通过ApplicationContext创建
		OBJ obj = this.context.getBean(objClass);
		obj.setPo(po);

		return obj;
	}

	/**
	 * 将数据库对象批量转换为逻辑对象
	 *
	 * @param poList
	 *            数据库对象列表
	 * @return 逻辑对象列表
	 */
	protected List<OBJ> convertToObjForList(List<PO> poList) {
		return ConverterUtil.convertList(poList, this.po2Obj);
	}

	/**
	 * 创建新的对象，同时保持到数据库
	 * 
	 * @param po
	 *            po
	 * @return 返回逻辑对象
	 * @throws BaseApiException
	 *             逻辑错误
	 */
	public OBJ create(PO po) throws BaseApiException {

		this.checkBeforeCreate(po);

		this.getDaoForObjFactory().insert(po);
		return this.convertToObj(po);
	}

	/**
	 * 根据主键获得对象
	 * 
	 * @param id
	 * @return
	 */
	public OBJ getById(ID id) {
		if (id == null) {
			return null;
		}

		PO po = this.getDaoForObjFactory().get(id);
		if (po != null) {
			return this.convertToObj(po);
		} else {
			return null;
		}
	}

	/**
	 * 根据主键获得对象，如果找不到数据就抛错
	 * 
	 * @param id
	 * @return
	 * @throws IdNotFoundException
	 */
	public OBJ getByIdNotNull(ID id) throws IdNotFoundException {
		OBJ obj = this.getById(id);
		if (obj == null) {
			throw new IdNotFoundException(this.objClass.getSimpleName(), id);
		} else {
			return obj;
		}
	}

	/**
	 * 翻页查询，返回逻辑对象的列表，
	 * 
	 * @param pageForm
	 *            翻页数据
	 * @param sqlStartWithForm
	 *            from开头的sql语句
	 * @param params
	 *            sql中的参数
	 * @return
	 */
	public PageQueryResult<OBJ> pageList(IPageForm pageForm, String sqlStartWithForm, Object... params) {
		PageQueryResult<PO> result = this.getDaoForObjFactory().findInPage(sqlStartWithForm, pageForm, params);
		return result.convert(this.po2Obj);
	}

	public PageQueryResult<OBJ> pageList(IPageForm form, WhereBuilder builder, String orderStr) {
		return this.pageList(form, builder, orderStr, this.po2Obj);
	}

	/**
	 * 进行翻页查询，返回指定的类型的列表，
	 * 
	 * @param pageForm
	 *            翻页数据
	 * @param builder
	 *            插件条件构造器
	 * @param orderStr
	 *            排序字符串
	 * @param converter
	 *            PO到指定类型的转换器
	 * @return
	 */
	public <TARGET> PageQueryResult<TARGET> pageList(IPageForm pageForm, WhereBuilder builder, String orderStr,
			IConverter<PO, TARGET> converter) {
		StringBuffer sql = new StringBuffer();

		// 默认是查询所有
		sql.append("from ").append(this.getDaoForObjFactory().getEntityClass().getSimpleName());

		Object[] params = null;

		if (builder != null) {
			// 如果有条件，就拼接 条件
			sql.append(" where").append(builder.getWhereSql());
			if (!builder.getParamsList().isEmpty()) {
				params = builder.getParams();
			}
		}

		sql.append(" order by ");
		if (StringUtils.hasText(orderStr)) {
			// 如果有排序字段，就按排序字段
			sql.append(orderStr);
		} else {
			// 否则就按主键反序
			sql.append(this.getDaoForObjFactory().getIdFieldName()).append(" desc");
		}

		PageQueryResult<PO> result = this.getDaoForObjFactory().findInPage(sql.toString(), pageForm, params);

		return result.convert(converter);

	}

	/**
	 * 根据查询条件，获取对象列表
	 * 
	 * @param sqlStartWithFrom
	 *            以from开头的sql，不能有 select字样
	 * @param params
	 *            参数
	 * @return 对象列表
	 */
	public List<OBJ> find(String sqlStartWithFrom, Object... params) {
		List<PO> poList = this.getDaoForObjFactory().find(sqlStartWithFrom, params);
		return ConverterUtil.convertList(poList, this.po2Obj);
	}

	public List<OBJ> findAll() {
		List<PO> poList = this.getDaoForObjFactory().findAll();
		return ConverterUtil.convertList(poList, this.po2Obj);
	}

	/**
	 * 根据查询条件，获取一个对象
	 * 
	 * @param sqlStartWithFrom
	 *            以from开头的sql，不能有 select字样
	 * @param params
	 *            参数
	 * @return 对象列表
	 */
	public OBJ findOne(String sqlStartWithFrom, Object... params) {
		List<PO> poList = this.getDaoForObjFactory().find(sqlStartWithFrom, params);
		if (poList != null && poList.size() > 0) {
			return this.convertToObj(poList.get(0));
		}
		return null;
	}

	/**
	 * 更新对象
	 * 
	 * @param obj
	 *            要保存的数据
	 * @param cleanListCache
	 */
	public void update(OBJ obj, boolean cleanListCache) throws BaseApiException {
		if (obj == null) {
			return;
		}
		// 更新之前，调用一个方法，方便子类进行检查
		obj.checkBeforeUpdate();

		// 检查后再更新
		this.getDaoForObjFactory().update(obj.getPo(), cleanListCache);
	}

	/**
	 * 返回基础的domain
	 * 
	 * @return
	 */
	protected abstract IObjFactoryDao<PO, ID> getDaoForObjFactory();

	/**
	 * 这个是危险操作，在数据库中删除数据
	 * 
	 * @param obj
	 *            要删除的对象
	 * @throws BaseApiException
	 *             逻辑错误
	 */
	public void delete(OBJ obj) throws BaseApiException {
		if (obj == null) {
			return;
		}

		// 删除前先检查一下
		obj.checkBeforeDelete();

		// 如果没有问题才真的删除
		this.getDaoForObjFactory().delete(obj.getPo());

		// 删除后可能需要检查一下
		obj.afterDelete();

	}


	/**
	 * 该方法在create前执行，子类可覆盖该方法，进行一些额外的逻辑判断
	 * 
	 * @param po
	 *            要创建的数据对象
	 * @throws BaseApiException
	 *             逻辑错误
	 */
	protected void checkBeforeCreate(PO po) throws BaseApiException {

	}

}
