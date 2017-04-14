package ${package};

import org.springframework.beans.factory.annotation.Autowired;

import com.linzi.common.beans.apiServer.BaseApiException;
import com.linzi.common.beans.form.IdForm;
import com.linzi.common.beans.apiServer.impl.CommonSuccessResponse;
import com.linzi.framework.db.PageQueryResult;
import com.linzi.framework.db.WhereBuilder;

import ${prop.basePackage}.api.impl.BaseApiImpl;
import ${prop.basePackage}.api.responses.${table.javaClassName}ViewResponse;

import ${prop.basePackage}.api.responses.${table.javaClassName}ListResponse;
import ${prop.basePackage}.form.${table.javaClassName}EditForm;
import ${prop.basePackage}.form.${table.javaClassName}ListForm;

import ${prop.basePackage}.logicObj.${table.javaClassName}Factory;
import ${prop.basePackage}.logicObj.${table.javaClassName}Obj;
import ${prop.basePackage}.logicObj.${table.javaClassName}ViewModel;

import ${prop.entityPackage}.${table.javaClassName};

/**
 * <pre>
 * ${table.javaClassName} api接口 实现类
 * 我们使用了带_CodeGen字样的不规范的类名，表面这个类是由代码生成器生成的，不要自己修改
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
public abstract class ${table.javaClassName}ApiImpl_CodeGen extends BaseApiImpl{


	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(${table.javaClassName}ApiImpl_CodeGen.class);

	@Autowired
	protected ${table.javaClassName}Factory factory;

	/**
	 * 查看
	 * 
	 * @param form
	 *            IdForm
	 * @return
	 * @throws BaseApiException
	 */
	public ${table.javaClassName}ViewResponse view(IdForm form) throws BaseApiException {

		log.debug("查看 ${table.javaClassName}, id={}" , form.getId());

		${table.javaClassName}Obj obj = this.factory.getByIdNotNull(form.getId());

		// 构建返回内容
		${table.javaClassName}ViewResponse res = new ${table.javaClassName}ViewResponse();
		res.setInfo(this.factory.getObj2ViewModelConverter().convert(obj));

		return res;
	}

	/**
	 * 删除
	 * 
	 * @param form
	 *            IdForm
	 * @return
	 * @throws BaseApiException
	 */
	public CommonSuccessResponse delete(IdForm form) throws BaseApiException {

		log.debug("删除 ${table.javaClassName}, id={}" , form.getId());

		// 先获取旧的对象
		${table.javaClassName}Obj old = this.factory.getByIdNotNull(form.getId());

		// 删除对象，不建议真的删除，因为有可能有外键
		this.factory.delete(old);

		return CommonSuccessResponse.DEFAULT;
	}

	/**
	 * 列表
	 * 
	 * @param form
	 *            查询表单
	 * @return
	 * @throws BaseApiException
	 */
	public ${table.javaClassName}ListResponse list(${table.javaClassName}ListForm form) throws BaseApiException {

		log.debug("查询 ${table.javaClassName} 列表");

		// 构建条件生成器
		WhereBuilder builder = WhereBuilder.create(form);
		// 将对象查询处理
		PageQueryResult<${table.javaClassName}Obj> objPage = this.factory.pageList(form, builder, null);

		// 将查询结果转为视图对象
		PageQueryResult<${table.javaClassName}ViewModel> page = objPage.convert(this.factory.getObj2ViewModelConverter());

		// 封装结果
		${table.javaClassName}ListResponse res = new ${table.javaClassName}ListResponse();
		res.setForm(form);
		res.setPage(page);

		return res;
	}

	/**
	 * 保存
	 * 
	 * @param form
	 *            ${table.javaClassName}EditForm
	 * @return
	 * @throws BaseApiException
	 */
	public CommonSuccessResponse save(${table.javaClassName}EditForm form) throws BaseApiException {
		if (form.getId() == null || form.getId() == 0) {
			// 如果没有id，就表示插入
			this.insert(form);

		} else {
			// 如果有 id，就表示更新
			this.update(form);
		}
		return CommonSuccessResponse.DEFAULT;
	}

	/**
	 * 新建对象
	 * 
	 * @param form
	 * @throws BaseApiException
	 */
	protected void insert(${table.javaClassName}EditForm form) throws BaseApiException {

		// 创建默认数值的po
		${table.javaClassName} po = this.factory.createDefaultPo();

		this.tranFormDataToPo(form, po, false);

		// 创建应用
		this.factory.create(po);
	}

	/**
	 * 将form中的值填写到po中去
	 * 
	 * @param form
	 *            编辑表单
	 * @param po
	 * @param isUpdate
	 *            是否是为了更新，更新时，有些字段是禁止传到po中的
	 */
	protected abstract void tranFormDataToPo(${table.javaClassName}EditForm form, ${table.javaClassName} po, boolean isUpdate);

	/**
	 * 更新
	 * 
	 * @param form
	 *            ${table.javaClassName}EditForm
	 * @throws BaseApiException
	 */
	protected void update(${table.javaClassName}EditForm form) throws BaseApiException {
		// 先获取旧的对象
		${table.javaClassName}Obj old = this.factory.getByIdNotNull(form.getId());

		// 根据表单填充数据
		this.tranFormDataToPo(form, old.getPo(), true);

		// 更新，并刷新cache
		this.factory.update(old, true);
	}
}
