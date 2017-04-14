package com.cfido.commons.spring.sendMail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * <pre>
 * 邮件smtp服务器 配置
 * 
 * </pre>
 * 
 * @author 梁韦江 2016年8月26日
 */
@ConfigurationProperties(prefix = "mail")
@ManagedResource(description = "发送邮箱配置", objectName = "Common配置:name=MailProperties")
public class SendMailProperties {

	private boolean ssl = true;
	private String host = "smtp.exmail.qq.com";
	private String user = "support@lin-zi.com";
	private String password = "lz0011223344";
	private int port = 465;
	private static String[] userArray = new String[10];

	private static String[] passwordArray = new String[10];

	// 用static修饰是为了在类加载的时候就创建实例
	private final static SendMailProperties instance = new SendMailProperties();

	// static修饰符帮助别人可以通过类直接调用这个方法
	public static SendMailProperties getInstance() {
		return instance;
	}

	// 私有的构造方法
	private SendMailProperties() {
		this.userAndPwdFiles();
	}

	/**
	 * 邮箱账号密码文件
	 * 
	 * @author 鲁炎
	 */
	private void userAndPwdFiles() {
		
		userArray[0] = "support@lin-zi.com";
		passwordArray[0] = "lz0011223344";
		
		userArray[1] = "support001@lin-zi.com";
		passwordArray[1] = "lz0011223344";
		
		userArray[2] = "support002@lin-zi.com";
		passwordArray[2] = "lz0011223344";
		
		userArray[3] = "support003@lin-zi.com";
		passwordArray[3] = "lz0011223344";
		
		userArray[4] = "support004@lin-zi.com";
		passwordArray[4] = "lz0011223344";
		
		userArray[5] = "support005@lin-zi.com";
		passwordArray[5] = "lz0011223344";
		
		userArray[6] = "support006@lin-zi.com";
		passwordArray[6] = "lz0011223344";
		
		userArray[7] = "support007@lin-zi.com";
		passwordArray[7] = "lz0011223344";
		
		userArray[8] = "support008@lin-zi.com";
		passwordArray[8] = "lz0011223344";
		
		userArray[9] = "support009@lin-zi.com";
		passwordArray[9] = "lz0011223344";
		
	}

	/**
	 * 循环排布用户名和密码
	 * 
	 * @author 鲁炎
	 */
	public static void reUserAndPwd() {
		String tempPwd = passwordArray[0];
		for (int j = 0; j < passwordArray.length; j++) {
			if (j < passwordArray.length - 1) {
				passwordArray[j] = passwordArray[j + 1];
			} else {
				passwordArray[passwordArray.length - 1] = tempPwd;
			}
		}
		String tempUser = userArray[0];
		for (int j = 0; j < userArray.length; j++) {
			if (j < userArray.length - 1) {
				userArray[j] = userArray[j + 1];
			} else {
				userArray[userArray.length - 1] = tempUser;
			}
		}
	}

	public String[] getUserArray() {
		return userArray;
	}

	public void setUserArray(String[] userArray) {
		SendMailProperties.userArray = userArray;
	}

	public String[] getPasswordArray() {
		return passwordArray;
	}

	public void setPasswordArray(String[] passwordArray) {
		SendMailProperties.passwordArray = passwordArray;
	}

	@ManagedAttribute(description = "是否用ssl")
	public boolean isSsl() {
		return ssl;
	}

	@ManagedAttribute()
	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	@ManagedAttribute(description = "smtp服务器地址")
	public String getHost() {
		return host;
	}

	@ManagedAttribute()
	public void setHost(String host) {
		this.host = host;
	}

	@ManagedAttribute(description = "登录smtp服务器的账号")
	public String getUser() {
		return user;
	}

	@ManagedAttribute()
	public void setUser(String user) {
		this.user = user;
	}

	@ManagedAttribute(description = "登录smtp服务器的密码")
	public String getPassword() {
		return password;
	}

	@ManagedAttribute()
	public void setPassword(String password) {
		this.password = password;
	}

	@ManagedAttribute(description = "smtp服务器端口")
	public int getPort() {
		return port;
	}

	@ManagedAttribute()
	public void setPort(int port) {
		this.port = port;
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
}
