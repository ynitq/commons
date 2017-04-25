package com.cfido.commons.spring.jmxInWeb;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import com.cfido.commons.loginCheck.IWebUser;
import com.cfido.commons.spring.jmxInWeb.core.IDomainNameFilter;
import com.cfido.commons.spring.jmxInWeb.core.JwWebUser;
import com.cfido.commons.spring.security.IUserServiceForRememberMe;

/**
 * <pre>
 * 配置
 * 
 * jmxInWeb.admin.account=admin
 * jmxInWeb.admin.password=linzi777
 * </pre>
 * 
 * @author 梁韦江 2016年8月26日
 */
@ConfigurationProperties(prefix = "jmxInWeb")
public class JmxInWebProperties {

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
	public class JmxInWebUserProvider implements IUserServiceForRememberMe {

		@Override
		public Class<? extends IWebUser> getSupportUserClassNames() {
			return JwWebUser.class;
		}

		@Override
		public IWebUser loadUserByUsername(String username) {
			Admin admin = JmxInWebProperties.this.admin;
			if (admin.isValid()) {
				return new JwWebUser(admin.account, admin.password);
			} else {
				return null;
			}
		}
	}

	/**
	 * <pre>
	 * 默认的domain过滤器，主要是过滤spring boot 中的过多的mbean
	 * </pre>
	 * 
	 * @author 梁韦江 2016年6月7日
	 */
	public class DefaultDomainNameFilter implements IDomainNameFilter {

		private final Set<String> ignoreSet = new HashSet<>();

		public DefaultDomainNameFilter() {
			this.addIgnoreDomain("Tomcat");
			this.addIgnoreDomain("Tomcat-1");
			this.addIgnoreDomain("com.sun.management");
			this.addIgnoreDomain("java.lang");
			this.addIgnoreDomain("java.nio");
			this.addIgnoreDomain("java.util.logging");
			this.addIgnoreDomain("JMImplementation");
		}

		@Override
		public boolean show(String domainName) {
			if (domainName == null) {
				return false;
			} else {
				return !this.ignoreSet.contains(domainName.toLowerCase());
			}
		}

		public void addIgnoreDomain(String domainName) {
			if (domainName != null) {
				this.ignoreSet.add(domainName.toLowerCase());
			}
		}

		public void removeIgnoreDomain(String domainName) {
			if (domainName != null) {
				this.ignoreSet.remove(domainName.toLowerCase());
			}
		}
	}

	private final Admin admin = new Admin();

	private final JmxInWebUserProvider adminUserProvider = new JmxInWebUserProvider();

	private final IDomainNameFilter domainNameFilter = new DefaultDomainNameFilter();

	public Admin getAdmin() {
		return admin;
	}

	public JmxInWebUserProvider getAdminUserProvider() {
		return adminUserProvider;
	}

	public IDomainNameFilter getDomainNameFilter() {
		return domainNameFilter;
	}

}
