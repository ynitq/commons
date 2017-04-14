package ${package};

import org.springframework.stereotype.Service;

import com.linzi.common.loginCheck.ANeedCheckLogin;

import ${prop.entityPackage}.${table.javaClassName};

import ${prop.basePackage}.api.I${table.javaClassName}Api;
import ${prop.basePackage}.form.${table.javaClassName}EditForm;
import ${prop.basePackage}.generated.${table.javaClassName}ApiImpl_CodeGen;
import ${prop.basePackage}.security.WebUser;


/**
 * <pre>
 * ${table.javaClassName} api接口实现类
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
@Service
@ANeedCheckLogin(userClass = WebUser.class)
public class ${table.javaClassName}ApiImpl extends ${table.javaClassName}ApiImpl_CodeGen implements I${table.javaClassName}Api {

	@Override
	protected void tranFormDataToPo(${table.javaClassName}EditForm form, ${table.javaClassName} po, boolean isUpdate) {
		// TODO 需要人工将表单的数据转到po中，请注意数据安全
<#list table.columns as column>
		// po.set${column.propNameU}(form.get${column.propNameU}()); <#if column.hasComment>// ${column.comment} </#if>
</#list>
	}
}
