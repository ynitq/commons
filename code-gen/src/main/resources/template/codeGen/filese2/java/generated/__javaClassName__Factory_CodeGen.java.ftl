package ${package};

import org.springframework.beans.factory.annotation.Autowired;

import com.linzi.common.beans.others.IConverter;
import com.linzi.framework.db.IObjFactoryDao;
import com.linzi.framework.logicObj.BaseObjFactory;

import ${prop.basePackage}.logicObj.${table.javaClassName}Obj;
import ${prop.basePackage}.logicObj.${table.javaClassName}ViewModel;

import ${prop.basePackage}.domains.${table.javaClassName}Domain;
import ${prop.entityPackage}.${table.javaClassName};

/**
 * <pre>
 * ${table.javaClassName} 工厂类基类
 * 我们使用了带_CodeGen字样的不规范的类名，表面这个类是由代码生成器生成的，不要自己修改
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
public abstract class ${table.javaClassName}Factory_CodeGen extends BaseObjFactory<${table.javaClassName}Obj, ${table.javaClassName}, Integer> {

	private final IConverter<${table.javaClassName}Obj, ${table.javaClassName}ViewModel> obj2ViewModelConverter = new IConverter<${table.javaClassName}Obj, ${table.javaClassName}ViewModel>() {
		@Override
		public ${table.javaClassName}ViewModel convert(${table.javaClassName}Obj src) {
			return new ${table.javaClassName}ViewModel(src);
		}
	};

	@Autowired
	protected ${table.javaClassName}Domain ${table.propName}Domain;

	@Override
	protected IObjFactoryDao<${table.javaClassName}, Integer> getDaoForObjFactory() {
		return ${table.propName}Domain;
	}

	/**
	 * 将obj转为视图对象的转换器
	 * 
	 * @param po
	 * @return
	 */
	public IConverter<${table.javaClassName}Obj, ${table.javaClassName}ViewModel> getObj2ViewModelConverter() {
		return obj2ViewModelConverter;
	}
}
