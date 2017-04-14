package com.cfido.commons.spring.dict.inf.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.cfido.commons.beans.apiServer.impl.CommonSuccessResponse;
import com.cfido.commons.beans.exceptions.security.InvalidPasswordException;
import com.cfido.commons.beans.form.LoginForm;
import com.cfido.commons.spring.dict.DictProperties;
import com.cfido.commons.spring.dict.core.DictAdminWebUser;
import com.cfido.commons.spring.dict.inf.IDictAdminUser;
import com.cfido.commons.spring.dict.inf.form.CreatePasswordForm;
import com.cfido.commons.spring.dict.inf.responses.UserInfoResponse;
import com.cfido.commons.spring.security.IUserServiceForRememberMe;
import com.cfido.commons.spring.security.LoginContext;
import com.cfido.commons.spring.security.RememberMeUserHandler;
import com.cfido.commons.utils.utils.PasswordEncoder;
import com.linzi.common.loginCheck.IWebUser;

/**
 * <pre>
 * 字典用户接口实现类
 * </pre>
 * 
 * @author 梁韦江 2016年11月16日
 */
@Service
public class DictAdminUserImpl implements IDictAdminUser {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DictAdminUserImpl.class);

	@Autowired
	private RememberMeUserHandler rememberMeUserHandler;

	@Autowired
	private LoginContext loginContext;

	@Autowired
	private DictProperties properties;

	/** 管理用户是否是在配置文件中定义的 */
	private boolean adminInPorp;

	@PostConstruct
	protected void init() {
		// 检查管理用户的认证提供者
		if (this.rememberMeUserHandler.getUserProvider(DictAdminWebUser.class) == null) {
			// 如果没有，就使用配置文件中的信息
			this.rememberMeUserHandler.addUserProvider(this.properties.getAdminUserProvider());
			this.adminInPorp = true;
			log.info("初始化 字典管理用户，账号由配置文件设置");
		} else {
			log.info("初始化 字典管理用户，账号由数据库管理");
			this.adminInPorp = false;
		}
	}

	/** 管理用户是否是在配置文件中定义的 */
	public boolean isAdminInPorp() {
		return adminInPorp;
	}

	@Override
	public UserInfoResponse getCurUser() {
		IWebUser user = this.loginContext.getUser(DictAdminWebUser.class);
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

		Assert.notNull(user);

		UserInfoResponse res = new UserInfoResponse();
		res.setAccount(user.getUsername());
		res.setLogined(true);
		return res;
	}

	@Override
	public UserInfoResponse login(LoginForm form) throws InvalidPasswordException {

		Assert.notNull(form.getAccount());
		Assert.notNull(form.getPassword());

		log.debug("字典管理用户 {} 登录后台", form.getAccount());

		// 寻找用户认证供应者
		IUserServiceForRememberMe userProvider = this.rememberMeUserHandler.getUserProvider(DictAdminWebUser.class);
		if (userProvider != null) {
			// 如果存在，就尝试获取用户
			IWebUser user = userProvider.loadUserByUsername(form.getAccount());
			if (user != null) {
				// 如过能获取用户,就检查密码
				PasswordEncoder.checkPassword(form.getPassword(), user.getPassword());

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

		this.loginContext.onLogout(DictAdminWebUser.class);

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
