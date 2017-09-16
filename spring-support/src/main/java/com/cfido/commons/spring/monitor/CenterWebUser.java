package com.cfido.commons.spring.monitor;

import com.cfido.commons.beans.monitor.UserInfoInCenterBean;
import com.cfido.commons.loginCheck.IWebUser;
import com.cfido.commons.spring.security.CommonAdminWebUser;

/**
 * <pre>
 * 中心服务器的用户
 * </pre>
 * 
 * @author 梁韦江
 */
public class CenterWebUser implements IWebUser {

	private final UserInfoInCenterBean userInfo;

	public CenterWebUser(UserInfoInCenterBean userInfo) {
		this.userInfo = userInfo;
	}

	@Override
	public boolean checkRights(String optId) {
		if (this.userInfo.isSuperuser()) {
			// 如果是超级用户，就直接返回真
			return true;
		} else if (userInfo.getRights() != null) {
			// 如果有权限设置，就按权限设置
			return userInfo.getRights().contains(optId);
		}
		return false;
	}

	@Override
	public String getUsername() {
		return userInfo.getAccount();
	}

	@Override
	public String getPassword() {
		return userInfo.getEncodedPassword();
	}

	public UserInfoInCenterBean getUserInfo() {
		return userInfo;
	}

	public CommonAdminWebUser createCommonAdminWebUser() {
		return new CommonAdminWebUser(this.getUsername(), this.getPassword());
	}

}
