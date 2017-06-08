package ${package};

import java.util.Date;

import com.linzi.common.annotation.api.AForm;
import com.cfido.commons.annotation.form.ABuildWhereFieldName;
import com.cfido.commons.annotation.form.ABuildWhereOptStr;
import com.linzi.common.beans.form.PageForm;

/**
 * <pre>
 * TODO ${table.javaClassName}列表的表单
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
@AForm
public class ${table.javaClassName}ListForm extends PageForm {
	private static final long serialVersionUID = 1L;

	//--------------- 属性 ---------------
<#list table.columns as column>
		<#if column.javaClassName='Date'> 
	@ABuildWhereFieldName(name = "${column.propName}")
	@ABuildWhereOptStr(optStr = ">=")
	private Date ${column.propName}Begin = new Date(); // 开始时间

	@ABuildWhereFieldName(name = "${column.propName}")
	@ABuildWhereOptStr(optStr = "<=")
	private Date ${column.propName}End = new Date(); //结束时间
		<#else>
	private ${column.javaClassName} ${column.propName};
		</#if>
</#list>

	//--------------- getter和 setter ---------------
<#list table.columns as column>
		<#if column.javaClassName='Date'>
	/** -------------- ${column.propName} 时间范围 -------------- */ 
	public void set${column.propNameU}Begin(Date ${column.propName}) {
		this.${column.propName}Begin = ${column.propName};
	}

	public Date get${column.propNameU}Begin() {
		return this.${column.propName}Begin;
	}

	public void set${column.propNameU}End(Date ${column.propName}) {
		this.${column.propName}End = ${column.propName};
	}

	public Date get${column.propNameU}End() {
		return this.${column.propName}End;
	}
	/** /-------------- ${column.propName} 时间范围 -------------- */ 
		<#else>
	public void set${column.propNameU}(${column.javaClassName} ${column.propName}) {
		this.${column.propName} = ${column.propName};
	}
	
	<#if column.hasComment>/** ${column.comment} */</#if>
	public ${column.javaClassName} ${column.getter}() {
		return this.${column.propName};
	}
		</#if>

</#list>

}
