<#if table.needListForm>
package ${package};

import ${prop.basePackage}.generated.${table.javaClassName}ListResponse_CodeGen;

/**
 * <pre>
 * ${table.javaClassName} 列表 的Response。这个类不会被覆盖
 * 如果没有ListForm，这个Response也不存在
 * </pre>
 * 
* @author 梁韦江 生成于 ${date}
 */
public class ${table.javaClassName}ListResponse extends ${table.javaClassName}ListResponse_CodeGen {
}
</#if>