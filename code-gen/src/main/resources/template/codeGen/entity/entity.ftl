package ${package};

<#list table.importList as import>
import ${import};
</#list>

/**
 * 表 ${table.name} ${table.comment}
 * <pre>
 * 生成时间 : ${date}  
 * </pre> 
 */
@Entity
@Table(name = "${table.name}")
@NamedQuery(name = "${table.javaClassName}.findAll", query = "SELECT a FROM ${table.javaClassName} a")
public class ${table.javaClassName} implements Serializable ${table.implementsStr} {
	private static final long serialVersionUID = 1L;

	//--------------- 主键 ---------------
<#if table.embeddedId>
	@EmbeddedId
	@AttributeOverrides({
<#assign index=0 /><#list table.id.columns as idColumn >	
			<#if (index>0)>,</#if>@AttributeOverride(name = "${idColumn.name}", column = @Column(name = "${idColumn.name}${idColumn.nullableStr} ${idColumn.lengthStr}))
<#assign index=index+1 /></#list>
	})
<#else>
	@Id
	${table.id.generatedValueStr}
	@Column(unique = true)
</#if>
	private ${table.id.javaClassName} id;

	//--------------- 属性 ---------------
<#list table.columns as column>
	<#if column.hasComment>/** ${column.comment} */
	@AComment("${column.comment}")</#if><#if column.hasNotNull>
	@NotNull</#if><#if column.temporal>
	${column.temporalStr}</#if>
	@Column(name = "${column.name}"${column.nullableStr} ${column.lengthStr})
	private ${column.javaClassName} ${column.propName};

</#list>
	
	//--------------- getter和 setter ---------------
	public void setId(${table.id.javaClassName} id) {
		this.id = id;
	}
	public ${table.id.javaClassName} getId() {
		return this.id;
	}
	
<#list table.columns as column>
	<#if column.hasComment>/** ${column.comment} */</#if>
	public void set${column.propNameU}(${column.javaClassName} ${column.propName}) {
		this.${column.propName} = ${column.propName};
	}
	<#if column.hasComment>/** ${column.comment} */</#if>
	@Column(name = "${column.name}"${column.nullableStr} ${column.lengthStr})
	public ${column.javaClassName} ${column.getter}() {
		return this.${column.propName};
	}
	
</#list>
}
