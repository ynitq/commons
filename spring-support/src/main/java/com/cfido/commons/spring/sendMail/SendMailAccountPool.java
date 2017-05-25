package com.cfido.commons.spring.sendMail;

import java.util.LinkedList;
import java.util.Properties;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.cfido.commons.spring.jmxInWeb.ADomainOrder;
import com.cfido.commons.spring.utils.CommonMBeanDomainNaming;

/**
 * <pre>
 * 邮件smtp服务器 配置
 * 
 * </pre>
 * 
 * @author 梁韦江 2016年8月26日
 */
@Service
@ManagedResource(description = "发送邮箱账户池")
@ADomainOrder(order = CommonMBeanDomainNaming.ORDER, domainName = CommonMBeanDomainNaming.DOMAIN_MAIL)
public class SendMailAccountPool {

	public class MailAccount {
		private final String host;
		private final String password;
		private final int port;
		private final boolean ssl;
		private final String user;
		private int counterSuccess = 0; // 成功次数
		private int counterError = 0; // 失败次数

		private JavaMailSenderImpl sender;

		public MailAccount(String user, String password, String host, int port, boolean ssl) {
			super();
			this.user = user;
			this.password = password;
			this.host = host;
			this.port = port;
			this.ssl = ssl;
		}

		public String getFrom(String from) {
			// 大多数的邮件服务器都要求发送人必须是邮箱
			String domainOfFrom = this.user;
			if (domainOfFrom.indexOf("@") < 0) {
				domainOfFrom = domainOfFrom + "@" + this.host;
			}

			String res = from + "<" + domainOfFrom + ">";
			return res;
		}

		public void incCounter(boolean success) {
			if (success) {
				this.counterSuccess++;
			} else {
				this.counterError++;
			}
		}

		public int getCounterTotal() {
			return this.counterError + this.counterSuccess;
		}

		public int getCounterSuccess() {
			return counterSuccess;
		}

		public int getCounterError() {
			return counterError;
		}

		public String getDesc() {
			return String.format("%s:%s@%s:%d %s",
					user, password, host, port,
					ssl ? "SSL" : "");
		}

		private JavaMailSenderImpl javaMailSenderSsl() {
			JavaMailSenderImpl ret = new JavaMailSenderImpl();
			Properties javaMailProperties = new Properties();
			javaMailProperties.put("mail.smtp.auth", true);
			javaMailProperties.put("mail.smtp.timeout", 60000);
			javaMailProperties.put("mail.smtp.starttls.enable", false);
			javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			javaMailProperties.put("mail.smtp.socketFactory.fallback", false);
			ret.setJavaMailProperties(javaMailProperties);
			return ret;
		}

		private JavaMailSenderImpl javaMailSender() {
			JavaMailSenderImpl ret = new JavaMailSenderImpl();
			Properties javaMailProperties = new Properties();
			javaMailProperties.put("mail.smtp.auth", true);
			javaMailProperties.put("mail.smtp.timeout", 60000);
			javaMailProperties.put("mail.smtp.starttls.enable", false);
			javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.SocketFactory");
			javaMailProperties.put("mail.smtp.socketFactory.fallback", false);
			ret.setJavaMailProperties(javaMailProperties);
			return ret;

		}

		JavaMailSenderImpl getSender() {
			if (this.sender == null) {
				if (this.ssl) {
					this.sender = this.javaMailSenderSsl();
				} else {
					this.sender = this.javaMailSender();
				}

				sender.setHost(this.host);
				sender.setPort(this.port);
				sender.setUsername(this.user);
				sender.setPassword(this.password);

			}
			return this.sender;
		}
	}

	private final LinkedList<MailAccount> accounts = new LinkedList<>();

	@ManagedOperation(description = "添加账户")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "user", description = "用户名"),
			@ManagedOperationParameter(name = "password", description = "密码"),
			@ManagedOperationParameter(name = "ssl", description = "ssh"),
			@ManagedOperationParameter(name = "host", description = "host"),
	})
	public synchronized void addAccount(String user, String password, boolean ssl, String host, int port) {
		Assert.hasText(user, "用户不能为空");
		Assert.hasText(password, "密码不能为空");
		Assert.hasText(host, "host不能为空");
		Assert.isTrue(port > 0, "端口应该大于0");

		MailAccount account = new MailAccount(user, password, host, port, ssl);
		this.accounts.add(account);
	}

	@ManagedOperation(description = "清理所有账号")
	public synchronized void clear() {
		this.accounts.clear();
	}

	@ManagedOperation(description = "根据用户名删除账户")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "user", description = "用户名"),
	})
	public synchronized String clearByUser(String user) {
		MailAccount target = null;
		for (MailAccount ma : accounts) {
			if (ma.user.equals(user)) {
				target = ma;
				break;
			}
		}

		if (target != null) {
			this.accounts.remove(target);
			return "删除成功";
		} else {
			return "找不到这个账号";
		}
	}

	@ManagedAttribute(description = "获取所有的账号")
	public LinkedList<MailAccount> getAccounts() {
		return accounts;
	}

	@ManagedAttribute(description = "登录smtp服务器的账号数量")
	public int getTotalAccount() {
		return this.accounts.size();
	}

	/**
	 * 轮流从多个账户中获取一个邮件账户，保证每个账户的发送量都是一样的
	 */
	protected synchronized MailAccount getMailAccount() {
		if (this.accounts.isEmpty()) {
			return null;
		}

		// 从头去一个
		MailAccount account = this.accounts.removeFirst();
		// 然后添加到最后
		this.accounts.add(account);
		return account;

	}

	@ManagedOperation(description = "添加腾讯邮箱的账户")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "user", description = "用户名"),
			@ManagedOperationParameter(name = "password", description = "密码"),
	})
	public void addAccount(String user, String password) {
		this.addAccount(user, password, true, "smtp.exmail.qq.com", 465);

	}

}
