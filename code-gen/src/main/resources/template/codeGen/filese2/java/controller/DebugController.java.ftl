/** 生成于 ${date} */
package ${package};

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.linzi.commons.apiServer.controller.BaseDebugController;

/**
 * <pre>
 * 开发时用的测试接口
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
@Controller
@RequestMapping("/dev")
public class DebugController extends BaseDebugController {

	@Override
	public String getApiUrlPrefix() {
		return "/api";
	}

	@Override
	public String getPageTitle() {
		return "代码生成例子";
	}

}
