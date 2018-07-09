package com.cfido.commons.spring.jmxInWeb.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.cfido.commons.annotation.api.AApiServerImpl;
import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.apiServer.impl.CommonSuccessResponse;
import com.cfido.commons.beans.exceptions.security.InvalidPasswordException;
import com.cfido.commons.beans.form.LoginForm;
import com.cfido.commons.loginCheck.ANeedCheckLogin;
import com.cfido.commons.spring.jmxInWeb.inf.IJmxInWeb;
import com.cfido.commons.spring.jmxInWeb.inf.form.JwChangeAttrForm;
import com.cfido.commons.spring.jmxInWeb.inf.form.JwInvokeOptForm;
import com.cfido.commons.spring.jmxInWeb.inf.form.JwObjectNameForm;
import com.cfido.commons.spring.jmxInWeb.inf.response.JwInvokeOptResponse;
import com.cfido.commons.spring.jmxInWeb.inf.response.JwMBeanInfoResponse;
import com.cfido.commons.spring.jmxInWeb.inf.response.JwMBeanListResponse;
import com.cfido.commons.spring.jmxInWeb.models.MBeanVo;
import com.cfido.commons.spring.security.CommonAdminWebUser;
import com.cfido.commons.spring.security.IWebUserProvider;
import com.cfido.commons.spring.security.LoginContext;
import com.cfido.commons.spring.security.RememberMeUserHandler;
import com.cfido.commons.utils.utils.PasswordEncoder;

/**
 * <pre>
 * 接口的实现类
 * </pre>
 * 
 * @author 梁韦江 2017年4月25日
 */
@Service
@AApiServerImpl
public class JwInfImpl implements IJmxInWeb {

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwInfImpl.class);

	@Autowired
	private RememberMeUserHandler rememberMeUserHandler;

	@Autowired
	private JmxInWebService service;

	@Autowired
	private LoginContext loginContext;

	@Override
	public CommonSuccessResponse login(LoginForm form) throws InvalidPasswordException {
		Assert.notNull(form.getAccount(), "账号不能为空");
		Assert.notNull(form.getPassword(), "密码不能为空");

		log.debug("管理用户 {} 登录后台", form.getAccount());

		// 寻找用户认证供应者
		IWebUserProvider<CommonAdminWebUser> userProvider = this.rememberMeUserHandler
				.getUserProvider(CommonAdminWebUser.class);
		if (userProvider != null) {
			// 如果存在，就尝试获取用户
			CommonAdminWebUser user = userProvider.loadUserByAccount(form.getAccount());
			if (user != null) {
				// 如过能获取用户,就检查密码
				PasswordEncoder.checkPassword(form.getPassword(), user.getEncryptedPassword());

				// 如果密码正确，就返回正常信息
				this.loginContext.onLoginSuccess(user, form.isRememberMe());

				return CommonSuccessResponse.DEFAULT;
			}
		}

		throw new InvalidPasswordException();
	}

	@Override
	public CommonSuccessResponse logout() {
		this.loginContext.onLogout(CommonAdminWebUser.class);
		return CommonSuccessResponse.DEFAULT;
	}

	@Override
	@ANeedCheckLogin(userClass = CommonAdminWebUser.class)
	public JwMBeanListResponse getMBeanList() throws BaseApiException {
		// 封装返回结果
		JwMBeanListResponse res = new JwMBeanListResponse();
		res.setList(this.service.getMBeanList());
		return res;
	}

	@Override
	@ANeedCheckLogin(userClass = CommonAdminWebUser.class)
	public CommonSuccessResponse changeAttr(JwChangeAttrForm form) throws BaseApiException {

		this.service.changeAttr(form);

		return CommonSuccessResponse.DEFAULT;
	}

	@Override
	@ANeedCheckLogin(userClass = CommonAdminWebUser.class)
	public JwInvokeOptResponse invokeOpt(JwInvokeOptForm form) throws BaseApiException {
		return this.service.invokeOpt(form.getObjectName(), form.getOptName(), form.getParamInfo());
	}

	@Override
	@ANeedCheckLogin(userClass = CommonAdminWebUser.class)
	public JwMBeanInfoResponse getMBeanInfo(JwObjectNameForm form) throws BaseApiException {
		MBeanVo vo = this.service.getMBeanInfo(form.getObjectName());

		JwMBeanInfoResponse res = new JwMBeanInfoResponse();
		res.setInfo(vo);
		return res;
	}

}
