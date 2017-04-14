package ${id.packageName};

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 表 ${id.tableName}的复合主键
 * 
 */
@Embeddable
public class ${id.javaClassName} implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	//--------------- 属性 ---------------
<#list id.columns as column>
	<#if column.hasComment>/** ${column.comment} */
	@AComment(comment="${column.comment}")</#if><#if column.nullable!=true>
	@NotNull</#if><#if column.temporal>
	${column.temporalStr}</#if>
	@Column(name = "${column.name}"${column.nullableStr} ${column.lengthStr})
	private ${column.javaClassName} ${column.propName}

</#list>
	
	//--------------- getter和 setter ---------------
<#list id.columns as column>
	public void set${column.propNameU}(${column.javaClassName} ${column.propName}) {
		this.${column.propName} = ${column.propName};
	}
	public ${column.javaClassName} get${column.propNameU}() {
		return this.${column.propName};
	}
	
</#list>

	public boolean equals(Object other) {
		还没写呢，equals和hashCode都必须写
	}

	public int hashCode() {
	}

}
