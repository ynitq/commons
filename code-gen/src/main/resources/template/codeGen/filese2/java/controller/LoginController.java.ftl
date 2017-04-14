/** 生成于 ${date} */
package ${package};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.linzi.commons.spring.security.LoginContext;
import ${prop.basePackage}.security.WebUser;

/**
 * <pre>
 * 登录
 * 
 * TODO 这是登录的例子，在根据项目实际需求修改了代码后，请删除这条TODO
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
@Controller
public class LoginController {

	@Autowired
	private LoginContext loginContext;

	@RequestMapping(value = "/login")
	public String login() {
		WebUser user = new WebUser();
		this.loginContext.onLoginSuccess(user);
		return "redirect:/";
	}

	@RequestMapping(value = "/logout")
	public String logout() {
		this.loginContext.onLogout(WebUser.class);
		return "redirect:/";
	}

}
