package ${package};

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cfido.commons.utils.logicObj.BasePoObj;
import ${prop.entityPackage}.${table.javaClassName};

/**
 * <pre>
 * ${table.javaClassName}表的逻辑对象
 * </pre>
 * 
 * @author 梁韦江 生成于: ${date}
 */
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ${table.javaClassName}Obj extends BasePoObj<${table.javaClassName}> {

	public ${table.javaClassName}ViewModel createModel() {
		return new ${table.javaClassName}ViewModel(this);
	}
}
