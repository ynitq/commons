package ${package};

import com.linzi.common.beans.apiServer.BaseResponse;
import ${prop.basePackage}.logicObj.${table.javaClassName}ViewModel;

/**
 * <pre>
 * ${table.javaClassName} 的查看Response。这个类不会被覆盖
 * 无论该表是否能查询，查看的Response总是存在的
 * </pre>
 * 
 * @author 梁韦江
 *  2016年8月25日
 */
public class ${table.javaClassName}ViewResponse extends BaseResponse {

	private ${table.javaClassName}ViewModel info;

	public ${table.javaClassName}ViewModel getInfo() {
		return info;
	}

	public void setInfo(${table.javaClassName}ViewModel info) {
		this.info = info;
	}

}
