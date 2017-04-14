package ${package};
import javax.validation.constraints.NotNull;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.linzi.common.annotation.api.AForm;
import com.linzi.common.beans.form.IdForm;
import com.linzi.common.annotation.bean.AComment;

/**
 * <pre>
 * TODO ${table.javaClassName}保存表单
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
@AForm
public class ${table.javaClassName}EditForm extends IdForm {

	//--------------- 属性 ---------------
<#list table.columns as column>
	<#if column.hasNotNull>@NotNull(message = "请输入${column.comment}")</#if>
	private ${column.javaClassNameInEditForm} ${column.propName};
</#list>

	//--------------- getter和 setter ---------------
<#list table.columns as column>
	<#if column.hasComment>@AComment(comment = "${column.comment}")</#if>
	public void set${column.propNameU}(${column.javaClassNameInEditForm} ${column.propName}) {
		this.${column.propName} = ${column.propName};
	}
	<#if column.hasComment>/** ${column.comment} */</#if>
	public ${column.javaClassNameInEditForm} ${column.getter}() {
		return this.${column.propName};
	}
</#list>
}
