package ${package};

import org.springframework.beans.factory.annotation.Autowired;

import com.linzi.commons.spring.security.LoginContext;

/**
 * <pre>
 * api 实现类的基类
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
public class BaseApiImpl {

	@Autowired
	protected LoginContext loginContext;

}
