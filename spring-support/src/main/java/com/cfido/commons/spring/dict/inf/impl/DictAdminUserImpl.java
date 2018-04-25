package com.cfido.commons.spring.dict.inf.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.cfido.commons.annotation.api.AApiServerImpl;
import com.cfido.commons.beans.apiServer.impl.CommonSuccessResponse;
import com.cfido.commons.beans.exceptions.security.InvalidPasswordException;
import com.cfido.commons.beans.form.LoginForm;
import com.cfido.commons.loginCheck.IWebUser;
import com.cfido.commons.spring.dict.inf.IDictAdminUser;
import com.cfido.commons.spring.dict.inf.form.CreatePasswordForm;
import com.cfido.commons.spring.dict.inf.responses.UserInfoResponse;
import com.cfido.commons.spring.security.CommonAdminWebUser;
import com.cfido.commons.spring.security.IWebUserProvider;
import com.cfido.commons.spring.security.LoginContext;
import com.cfido.commons.spring.security.RememberMeUserHandler;
import com.cfido.commons.utils.utils.PasswordEncoder;

/**
 * <pre>
 * 字典用户接口实现类
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
@Service
@AApiServerImpl
public class DictAdminUserImpl implements IDictAdminUser {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DictAdminUserImpl.class);

	@Autowired
	private RememberMeUserHandler rememberMeUserHandler;

	@Autowired
	private LoginContext loginContext;

	@Override
	public UserInfoResponse getCurUser() {
		IWebUser user = this.loginContext.getUser(CommonAdminWebUser.class);
		if (user != null) {
			// 如果已经登录了，就返回用户信息
			return this.getResponse(user);
		} else {
			// 如果没有登陆，就设置状态为：未登录
			UserInfoResponse res = new UserInfoResponse();
			res.setLogined(false);
			return res;
		}
	}

	private UserInfoResponse getResponse(IWebUser user) {

		Assert.notNull(user, "user不能为空");

		UserInfoResponse res = new UserInfoResponse();
		res.setAccount(user.getAccount());
		res.setLogined(true);
		return res;
	}

	@Override
	public UserInfoResponse login(LoginForm form) throws InvalidPasswordException {

		Assert.notNull(form.getAccount(), "账号不能为空");
		Assert.notNull(form.getPassword(), "密码不能为空");

		log.debug("字典管理用户 {} 登录后台", form.getAccount());

		// 寻找用户认证供应者
		IWebUserProvider<CommonAdminWebUser> userProvider = this.rememberMeUserHandler
				.getUserProvider(CommonAdminWebUser.class);
		if (userProvider != null) {
			// 如果存在，就尝试获取用户
			IWebUser user = userProvider.loadUserByAccount(form.getAccount());
			if (user != null) {
				// 如过能获取用户,就检查密码
				PasswordEncoder.checkPassword(form.getPassword(), user.getEncryptedPassword());

				// 如果密码正确，就返回正常信息
				this.loginContext.onLoginSuccess(user, form.isRememberMe());

				return this.getResponse(user);
			}
		}

		throw new InvalidPasswordException();
	}

	@Override
	public UserInfoResponse logout() {

		log.debug("logout");

		this.loginContext.onLogout(CommonAdminWebUser.class);

		UserInfoResponse res = new UserInfoResponse();
		res.setLogined(false);
		return res;
	}

	@Override
	public CommonSuccessResponse passwordDemo(CreatePasswordForm form) {

		// 生成加密后的密码
		String encoded = PasswordEncoder.encodePassword(form.getPassword());

		CommonSuccessResponse res = new CommonSuccessResponse();
		res.setMessage(encoded);
		return res;
	}

}
