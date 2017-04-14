package ${package};

import com.linzi.common.beans.apiServer.BaseResponse;
import ${prop.basePackage}.logicObj.${table.javaClassName}ViewModel;

/**
 * <pre>
 * 查看${table.javaClassName}的Response
 * 我们使用了带_CodeGen字样的不规范的类名，表面这个类是由代码生成器生成的，不要自己修改
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
public abstract class ${table.javaClassName}ViewResponse_CodeGen extends BaseResponse {

	private ${table.javaClassName}ViewModel info;

	public ${table.javaClassName}ViewModel getInfo() {
		return info;
	}

	public void setInfo(${table.javaClassName}ViewModel info) {
		this.info = info;
	}

}
