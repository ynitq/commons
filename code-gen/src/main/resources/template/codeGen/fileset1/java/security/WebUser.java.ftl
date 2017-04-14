package ${package};

import com.linzi.common.loginCheck.IWebUser;

/**
 * <pre>
 * 模拟一个 叫admin的用户， 密码是1
 * </pre>
 * 
 * @author 梁韦江 生成于 ${date}
 */
public class WebUser implements IWebUser {

	@Override
	public boolean checkRights(String optId) {
		return true;
	}

	@Override
	public String getUsername() {
		return "admin";
	}

	@Override
	public String getPassword() {
		return "1";
	}
}
