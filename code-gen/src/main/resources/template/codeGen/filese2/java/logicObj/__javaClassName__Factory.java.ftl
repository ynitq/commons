package ${package};

import org.springframework.stereotype.Service;

import com.linzi.common.beans.apiServer.BaseApiException;
import ${prop.entityPackage}.${table.javaClassName};
import ${prop.basePackage}.generated.${table.javaClassName}Factory_CodeGen;

/**
 * <pre>
 * ${table.javaClassName}逻辑对象的工厂类，这个文件在生成时默认不覆盖
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
@Service
public class ${table.javaClassName}Factory extends ${table.javaClassName}Factory_CodeGen {

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
