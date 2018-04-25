package com.cfido.commons.spring.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.cfido.commons.spring.security.CommonAdminWebUser;
import com.cfido.commons.spring.security.IWebUserProvider;

/**
 * <pre>
 * CenterWebUser 用户数据供应者
 * </pre>
 * 
 * @author 梁韦江
 */
@Service
@ConditionalOnProperty(prefix = "monitorClient", name = "enableCenterUser", havingValue = "true", matchIfMissing = false)
public class CenterCommonAdminWebUserProvider implements IWebUserProvider<CommonAdminWebUser> {

	@Autowired
	private CenterWebUserProvider service;

	@Override
	public Class<CommonAdminWebUser> getSupportUserClassNames() {
		return CommonAdminWebUser.class;
	}

	@Override
	public CommonAdminWebUser loadUserByAccount(String account) {
		CenterWebUser user = this.service.loadUserByAccount(account);
		if (user != null && user.checkRights(CommonAdminWebUser.RIGHTS_NAME)) {
			// 如果有通用管理员的权限，就返回：可登陆
			return user.createCommonAdminWebUser();
		} else {
			return null;
		}
	}
}
