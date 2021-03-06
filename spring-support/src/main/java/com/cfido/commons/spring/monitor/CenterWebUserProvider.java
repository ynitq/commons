package com.cfido.commons.spring.monitor;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.cfido.commons.beans.apiServer.BaseApiException;
import com.cfido.commons.beans.exceptions.security.CenterServerErrorException;
import com.cfido.commons.beans.exceptions.security.UserNotFoundException;
import com.cfido.commons.beans.monitor.UserInfoInCenterBean;
import com.cfido.commons.loginCheck.IWebUser;
import com.cfido.commons.spring.security.IUserServiceForRememberMe;

/**
 * <pre>
 * CenterWebUser 用户数据供应者
 * </pre>
 * 
 * @author 梁韦江
 */
@Service
@ConditionalOnProperty(prefix = "monitorClient", name = "enableCenterUser", havingValue = "true", matchIfMissing = false)
public class CenterWebUserProvider implements IUserServiceForRememberMe {

	@Autowired
	private MonitorClientService service;

	@Override
	public Class<? extends IWebUser> getSupportUserClassNames() {
		return CenterWebUser.class;
	}

	@Override
	public CenterWebUser loadUserByUsername(String account) {
		try {
			return this.loadFromCenter(account);
		} catch (BaseApiException e) {
			return null;
		}
	}

	/** 从中心服务器获取用户，返回一定不为空 */
	public CenterWebUser loadFromCenter(String account) throws BaseApiException {
		try {
			UserInfoInCenterBean userInfo = this.service.getUserInfoFromCenter(account);
			if (userInfo != null) {
				return new CenterWebUser(userInfo);
			}
		} catch (IOException e) {
			throw new CenterServerErrorException();
		}

		throw new UserNotFoundException();
	}
}
