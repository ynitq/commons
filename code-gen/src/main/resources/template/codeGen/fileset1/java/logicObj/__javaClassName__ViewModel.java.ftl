package ${package};

import com.linzi.framework.logicObj.BaseViewModel;
import ${prop.entityPackage}.${table.javaClassName};

/**
 * <pre>
 * ${table.javaClassName}对象的 view model专门用于传递数据给页面。
 * 这个类虽然有po，但这个po是拷贝了一份，所以可以自由的修改内容
 * 
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
public class ${table.javaClassName}ViewModel extends BaseViewModel<${table.javaClassName}Obj, ${table.javaClassName}> {

	public ${table.javaClassName}ViewModel() {
		super();
	}

	public ${table.javaClassName}ViewModel(${table.javaClassName}Obj obj) {
		super(obj);
	}
}
