package ${package};

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.linzi.common.beans.apiServer.BaseApiException;
import com.linzi.common.beans.others.IConverter;
import com.linzi.framework.db.IObjFactoryDao;
import com.linzi.framework.logicObj.BaseObjFactory;

import ${prop.entityPackage}.${table.javaClassName};

import ${prop.basePackage}.logicObj.${table.javaClassName}Obj;
import ${prop.basePackage}.logicObj.${table.javaClassName}ViewModel;
import ${prop.basePackage}.domains.${table.javaClassName}Domain;

/**
 * <pre>
 * ${table.javaClassName}逻辑对象的工厂类，这个文件在生成时默认不覆盖
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
@Service
public class ${table.javaClassName}Factory extends BaseObjFactory<${table.javaClassName}Obj, ${table.javaClassName}, Integer> {

	private final IConverter<${table.javaClassName}Obj, ${table.javaClassName}ViewModel> obj2ViewModelConverter = new IConverter<${table.javaClassName}Obj, ${table.javaClassName}ViewModel>() {
		@Override
		public ${table.javaClassName}ViewModel convert(${table.javaClassName}Obj src) {
			return src.createModel();
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

	public ${table.javaClassName} createDefaultPo() {
		${table.javaClassName} po = new ${table.javaClassName}();

		// TODO 创建新的${table.javaClassName}时，设置默认值

		return po;
	}

	@Override
	public void delete(${table.javaClassName}Obj obj) throws BaseApiException {
		// TODO 需要人工干预是否真的从数据库中删除
		super.delete(obj);
	}

}
