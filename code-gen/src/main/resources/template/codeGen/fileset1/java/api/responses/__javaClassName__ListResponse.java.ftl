package ${package};

import com.linzi.common.beans.apiServer.BaseResponse;
import com.linzi.framework.db.PageQueryResult;

import ${prop.basePackage}.logicObj.${table.javaClassName}ViewModel;
import ${prop.basePackage}.form.${table.javaClassName}ListForm;

/**
 * <pre>
 * ${table.javaClassName} 列表 的Response。这个类不会被覆盖
 * 如果没有ListForm，这个Response也不存在
 * </pre>
 * 
* @author 梁韦江 生成于 ${date}
 */
public class ${table.javaClassName}ListResponse extends BaseResponse {

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
