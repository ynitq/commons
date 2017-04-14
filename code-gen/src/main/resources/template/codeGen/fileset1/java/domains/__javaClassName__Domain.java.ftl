package ${package};

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.linzi.framework.db.BaseDomainWithCache;
import com.linzi.framework.db.ICommonDao;
import ${prop.entityPackage}.${table.javaClassName};


/**
 * <pre>
 * 操作${table.javaClassName}表的domain, 
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
@Component
public class ${table.javaClassName}Domain extends BaseDomainWithCache<${table.javaClassName}, ${table.id.javaClassName}>  {
	@Autowired
	private ICommonDao commonDao;

	@Override
	protected ICommonDao getCommonDao() {
		return this.commonDao;
	}

}

