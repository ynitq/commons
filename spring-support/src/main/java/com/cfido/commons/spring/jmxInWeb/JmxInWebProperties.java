package com.cfido.commons.spring.jmxInWeb;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.cfido.commons.spring.jmxInWeb.core.IDomainNameFilter;
import com.cfido.commons.spring.security.IWebUserProvider;

/**
 * <pre>
 * 可配置参数
 * 
 * </pre>
 * 
 * @see IWebUserProvider
 *      如果spring能找到一个实现了IUserServiceForRememberMe接口的服务，并且该接口是提供给{@link JwWebUser}的，则使用这个服务来验证用户，否则就用系统内置的
 * 
 * @author 梁韦江 2016年8月26日
 */
@ConfigurationProperties(prefix = "jmxInWeb")
public class JmxInWebProperties {

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

	private final IDomainNameFilter domainNameFilter = new DefaultDomainNameFilter();

	public IDomainNameFilter getDomainNameFilter() {
		return domainNameFilter;
	}

}
