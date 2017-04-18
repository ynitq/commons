package com.cfido.commons.spring.dict;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.cfido.commons.loginCheck.IWebUser;
import com.cfido.commons.spring.dict.core.DictAdminWebUser;
import com.cfido.commons.spring.security.IUserServiceForRememberMe;

/**
 * <pre>
 * 字典 配置
 * 
 * 默认参数
 * dict.dataDir = dict
 * dict.xmlFile=dict
 * 
 * dict.savePeriod = 60
 * 
 * dict.apiUrlPrefix = /api
 * 
 * dict.admin.account=
 * dict.admin.password=
 * dict.admin.pageTitle=dict.admin.pageTitle
 * </pre>
 * 
 * @author 梁韦江 2016年8月26日
 */
@ConfigurationProperties(prefix = "dict")
public class DictProperties {

	/** 导出xml文件的url */
	public final static String EXPORT_URL = "/dict/export/dict.xml";

	/** 管理页面的url */
	public final static String MANAGER_URL = "/dict/manager";
	
	
	/** Js的url */
	public final static String DICT_JS = "/dict/dictJs.js";

	/**
	 * <pre>
	 * 默认的管理用户账号
	 * </pre>
	 * 
	 * @author 梁韦江 2016年11月16日
	 */
	public static class Admin {
		private String account = "admin";

		/** 默认密码是 linzi777 **/
		private String password = "d70623d9b2ea2d300662bf27c75b45bd";

		private String pageTitle = "dict.admin.pageTitle";

		public String getPageTitle() {
			return pageTitle;
		}

		public void setPageTitle(String pageTitle) {
			this.pageTitle = pageTitle;
		}

		public String getAccount() {
			return account;
		}

		public String getPassword() {
			return password;
		}

		public boolean isValid() {
			return StringUtils.hasText(account) && StringUtils.hasText(password);
		}

		public void setAccount(String account) {
			this.account = account;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

	/**
	 * <pre>
	 * 默认的 用户数据供应者，用于记录用户信息到cookie
	 * </pre>
	 * 
	 * @author 梁韦江 2016年11月16日
	 */
	public class AdminUserProvider implements IUserServiceForRememberMe {

		@Override
		public String[] getSupportUserClassNames() {
			return new String[] { DictAdminWebUser.class.getName(), };
		}

		@Override
		public IWebUser loadUserByUsername(String username) {
			Admin admin = DictProperties.this.admin;
			if (admin.isValid()) {
				return new DictAdminWebUser(admin.account, admin.password);
			} else {
				return null;
			}
		}

	}

	/** xml文件名 */
	private String xmlFile = "dict";

	/** 数据文件的目录 */
	private String dataDir = "dictData";

	/** 定时保存数据的周期 单位：秒 */
	private long savePeriod = 60 * 5;

	private String apiUrlPrefix = "/api";

	public String getDataDir() {
		return dataDir;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

	private final Admin admin = new Admin();

	private final AdminUserProvider adminUserProvider = new AdminUserProvider();

	public Admin getAdmin() {
		return admin;
	}

	public AdminUserProvider getAdminUserProvider() {
		return adminUserProvider;
	}

	public long getSavePeriod() {
		return savePeriod;
	}

	public String getXmlFile() {
		return xmlFile;
	}

	/**
	 * 获得xml文件的全路径
	 */
	public String getXmlFullPath() {
		String fileName = String.format("%s/%s.xml", this.dataDir, this.xmlFile);
		return fileName;
	}

	public String getApiUrlPrefix() {
		return apiUrlPrefix;
	}

	public void setApiUrlPrefix(String apiUrlPrefix) {
		this.apiUrlPrefix = apiUrlPrefix;
	}

	/**
	 * 获得xml 备份文件的全路径, 例如 dictData/backup.20161117.xml
	 */
	public String getBackupFileFullPath() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		String fileName = String.format("%s/backup.%s.xml", this.dataDir, sdf.format(new Date()));
		return fileName;
	}

	public void setSavePeriod(long savePeriod) {
		this.savePeriod = savePeriod;
	}

	public void setXmlFile(String fileName) {
		Assert.hasText(fileName);

		this.xmlFile = fileName;
	}
}
