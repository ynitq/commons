/** 生成于 ${date} */
package ${package};

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linzi.common.beans.apiExceptions.InvalidLoginStatusException;
import com.linzi.common.loginCheck.ANeedCheckLogin;
import com.linzi.commons.apiServer.adapter.AjaxAdapter;
import com.linzi.commons.apiServer.beans.ApiMethodInfo;
import com.linzi.commons.apiServer.beans.ApiServerInitException;
import com.linzi.commons.spring.debugMode.DebugModeProperties;
import com.linzi.commons.spring.security.LoginContext;
import ${prop.basePackage}.security.WebUser;

import ${prop.basePackage}.api.impl.*;

/**
 * <pre>
 * API接口Controller
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
@RestController
@RequestMapping("/api")
@ANeedCheckLogin(userClass = WebUser.class)
public class APIController extends AjaxAdapter {

	@Autowired
	private DebugModeProperties debugModeProperties;

	@Autowired
	protected LoginContext context;

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(APIController.class);

<#list context.tables as table>
	@Autowired
	private ${table.javaClassName}ApiImpl ${table.propName};
	
</#list>

	/**
	 * 初始化APIMap
	 */
	@PostConstruct
	void init() throws Exception {
		log.info("初始化APIMap");
		try {
<#list context.tables as table>
			this.addImplToMap(${table.propName});
</#list>
		} catch (ApiServerInitException e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	protected void onBeforeInvoke(HttpServletRequest req, HttpServletResponse resp, ApiMethodInfo methodInfo)
			throws InvalidLoginStatusException {

		if (methodInfo.isNeedLogin()) {
			// 如果这个方法需要登录，就检查一下是否登录了
			methodInfo.checkRightUseLoginCheck(context);
		}
	}

	@Override
	protected boolean isNeedCreateMockData() {
		return this.debugModeProperties.isDebugMode();
	}

}
