package ${package};

import com.linzi.common.beans.apiServer.BaseResponse;
import com.linzi.framework.db.PageQueryResult;

import ${prop.basePackage}.logicObj.${table.javaClassName}ViewModel;
import ${prop.basePackage}.form.${table.javaClassName}ListForm;

/**
 * <pre>
 * ${table.javaClassName} list 的返回结果
 * 我们使用了带_CodeGen字样的不规范的类名，表面这个类是由代码生成器生成的，不要自己修改
 * </pre>
 * 
* @author 梁韦江 生成于 ${date}
 */
public abstract class ${table.javaClassName}ListResponse_CodeGen extends BaseResponse {

	private ${table.javaClassName}ListForm form;

	private PageQueryResult<${table.javaClassName}ViewModel> page;

	public ${table.javaClassName}ListForm getForm() {
		return form;
	}

	public void setForm(${table.javaClassName}ListForm form) {
		this.form = form;
	}

	public PageQueryResult<${table.javaClassName}ViewModel> getPage() {
		return page;
	}

	public void setPage(PageQueryResult<${table.javaClassName}ViewModel> page) {
		this.page = page;
	}
}
