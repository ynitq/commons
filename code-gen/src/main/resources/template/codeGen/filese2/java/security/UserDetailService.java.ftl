package ${package};

import org.springframework.stereotype.Service;

import com.linzi.common.loginCheck.IWebUser;
import com.linzi.commons.spring.security.IUserServiceForRememberMe;
import com.linzi.commons.spring.security.LoginCheckInterceptor;

/**
 * <pre>
 * 模拟一个用户获取者
 * </pre>
 * 
 * @see LoginCheckInterceptor 这个类用于为LoginCheckInterceptor提供用户数据
 * 
 * @author 梁韦江 生成于 ${date}
 */
@Service
public class UserDetailService implements IUserServiceForRememberMe {

	@Override
	public IWebUser loadUserByUsername(String username) {
		// TODO 模拟登陆用户，在实际项目中，这里要修改，真的从数据库中获取
		return new WebUser();
	}

	@Override
	public String[] getSupportUserClassNames() {
		return new String[] {
				WebUser.class.getName(),
		};
	}
}
