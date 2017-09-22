package com.cfido.commons.beans.monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * 在中心服务器的用户信息，json格式
 * </pre>
 * 
 * @author 梁韦江
 */
public class UserInfoInCenterBean {

	/** 账号 */
	private String account;
	/** 名字 */
	private String name;
	/** 加密后的密码 */
	private String encodedPassword;

	/** 额外信息 */
	private Map<String, String> exInfo;

	/** 是否超级用户 */
	private boolean superuser;

	/** 权限 */
	private Set<String> rights;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEncodedPassword() {
		return encodedPassword;
	}

	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
	}

	public Map<String, String> getExInfo() {
		return exInfo;
	}

	public void setExInfo(Map<String, String> exInfo) {
		this.exInfo = exInfo;
	}

	public boolean isSuperuser() {
		return superuser;
	}

	public void setSuperuser(boolean superuser) {
		this.superuser = superuser;
	}

	public Set<String> getRights() {
		return rights;
	}

	public void setRights(Set<String> rights) {
		this.rights = rights;
	}

	/** 增加额外的信息 */
	public void addExInfo(String key, String value) {
		if (key == null || value == null) {
			return;
		}

		if (this.exInfo == null) {
			this.exInfo = new HashMap<>();
		}

		exInfo.put(key, value);
	}

}
