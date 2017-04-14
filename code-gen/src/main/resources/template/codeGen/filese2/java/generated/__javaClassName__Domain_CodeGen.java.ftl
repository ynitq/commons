package ${package};

import org.springframework.beans.factory.annotation.Autowired;

import com.linzi.framework.db.BaseDomainWithCache;
import com.linzi.framework.db.ICommonDao;
import ${prop.entityPackage}.${table.javaClassName};

/**
 * <pre>
 * 操作${table.javaClassName}表的domain,
 * 我们使用了带_CodeGen字样的不规范的类名，表面这个类是由代码生成器生成的，不要自己修改
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
public abstract class ${table.javaClassName}Domain_CodeGen extends BaseDomainWithCache<${table.javaClassName}, ${table.id.javaClassName}> {

	@Autowired
	private ICommonDao commonDao;

	@Override
	protected ICommonDao getCommonDao() {
		return this.commonDao;
	}

}
