package com.cfido.commons.spring.monitor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.form.LoginForm;
import com.cfido.commons.spring.security.CommonAdminWebUser;
import com.cfido.commons.spring.security.LoginContext;
import com.cfido.commons.utils.utils.PasswordEncoder;

/**
 * <pre>
 * 用户中心的用户登录
 * </pre>
 * 
 * @author 梁韦江
 */
@Service
@ConditionalOnProperty(prefix = "monitorClient", name = "enableCenterUser", havingValue = "true", matchIfMissing = false)
public class CenterWebUserLoginService {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CenterWebUserLoginService.class);

	@Autowired
	private CenterWebUserProvider userProvider;

	@Autowired
	private LoginContext loginContext;

	public void login(LoginForm form) throws BaseApiException {
		this.login(form.getAccount(), form.getPassword(), form.isRememberMe());
	}

	@PostConstruct
	protected void init() {
		log.info("中心用户: 用户系统有中心服务器控制");
	}

	public void login(String account, String password, boolean rememberMe) throws BaseApiException {
		Assert.hasText(account, "账号不能为空");
		Assert.hasText(password, "密码不能为空");

		// 获取用户
		CenterWebUser user = this.userProvider.loadFromCenter(account);

		// 检查密码是否正确
		PasswordEncoder.checkPassword(password, user.getPassword());

		this.loginContext.onLoginSuccess(user, rememberMe);

		if (user.getUserInfo().isSuperuser()) {
			// 如果是超级用户，可登陆jmx和dict
			this.loginContext.onLoginSuccess(user.createCommonAdminWebUser(), rememberMe);
		}
	}

	public void logout() {
		this.loginContext.onLogout(CenterWebUser.class);
		this.loginContext.onLogout(CommonAdminWebUser.class);
	}

}
