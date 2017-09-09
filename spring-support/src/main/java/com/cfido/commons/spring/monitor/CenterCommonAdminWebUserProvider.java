package com.cfido.commons.spring.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.cfido.commons.loginCheck.IWebUser;
import com.cfido.commons.spring.security.CommonAdminWebUser;
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
public class CenterCommonAdminWebUserProvider implements IUserServiceForRememberMe {

	@Autowired
	private CenterWebUserProvider service;

	@Override
	public Class<? extends IWebUser> getSupportUserClassNames() {
		return CommonAdminWebUser.class;
	}

	@Override
	public CommonAdminWebUser loadUserByUsername(String account) {
		CenterWebUser user = this.service.loadUserByUsername(account);
		if (user != null) {
			return user.createCommonAdminWebUser();
		} else {
			return null;
		}
	}
}
