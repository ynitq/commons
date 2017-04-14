package ${package};

import com.linzi.common.annotation.api.AClass;
import com.linzi.common.annotation.api.AMethod;
import com.linzi.common.beans.apiServer.BaseApiException;
import com.linzi.common.beans.form.IdForm;
import com.linzi.common.beans.apiServer.impl.CommonSuccessResponse;
import ${prop.basePackage}.api.responses.${table.javaClassName}ViewResponse;

import ${prop.basePackage}.api.responses.${table.javaClassName}ListResponse;
import ${prop.basePackage}.form.${table.javaClassName}ListForm;
import ${prop.basePackage}.form.${table.javaClassName}EditForm;

/**
 * <pre>
 * ${table.javaClassName} 相关ajax接口
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
@AClass(value = "${table.propName}")
public interface I${table.javaClassName}Api {

	@AMethod(comment = "列表", saveFormToSession = true)
	${table.javaClassName}ListResponse list(${table.javaClassName}ListForm form) throws BaseApiException;
	
	@AMethod(comment = "保存")
	CommonSuccessResponse save(${table.javaClassName}EditForm form) throws BaseApiException;

	@AMethod(comment = "删除")
	CommonSuccessResponse delete(IdForm form) throws BaseApiException;

	@AMethod(comment = "查看")
	${table.javaClassName}ViewResponse view(IdForm form) throws BaseApiException;

}
