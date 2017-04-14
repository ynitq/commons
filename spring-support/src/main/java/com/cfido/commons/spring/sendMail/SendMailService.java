package com.cfido.commons.spring.sendMail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.cfido.commons.utils.threadPool.IMyTask;
import com.cfido.commons.utils.utils.LogUtil;

/**
 * <pre>
 * 发邮件服务
 * </pre>
 * 
 * @author 梁韦江 2015年7月21日
 */
@Service
public class SendMailService {

	public final static SendMailService instance = new SendMailService();
	
	@Autowired
	private SendMailAutoConfig mailConfig;

	private SendMailProperties prop = SendMailProperties.getInstance();

	private class SendMailTask implements IMyTask {
		private final String from;
		private final String subject;
		private final String text;
		private final String to;

		public SendMailTask(String from, String to, String subject, String text) {
			super();
			this.from = from;
			this.to = to;
			this.subject = subject;
			this.text = text;
		}

		@Override
		public void afterRun() {
		}

		@Override
		public String getUniqueId() {
			return "SendMailTask:\t" + this.to;
		}

		@Override
		public void run() {
			SendMailService.this.doSendMail(this.from, this.to, this.subject, this.text);
		}

	}

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SendMailService.class);

	@Autowired
	private SendMailThreadPool sendMailThreadPool;

	public void sendMail(String from, String to, String subject, String text) {
		SendMailTask tsk = new SendMailTask(from, to, subject, text);
		this.sendMailThreadPool.addNewTask(tsk);
	}

	/**
	 * 获取发送者
	 */
	private JavaMailSender getMailSender() {
		
		JavaMailSenderImpl mailSender;
		if (this.prop.isSsl()) {
			mailSender = this.mailConfig.javaMailSenderSsl();
		} else {
			mailSender = this.mailConfig.javaMailSender();
		}

		// 因为我们可以通过jmx修改配置，所有我们需要在每次发送邮件时都重新设置最新的参数
		String[] userArray = prop.getUserArray();
		String[] passwordArray = prop.getPasswordArray();
		mailSender.setHost(this.prop.getHost());
		mailSender.setPort(this.prop.getPort());
		mailSender.setUsername(userArray[0]);
		mailSender.setPassword(passwordArray[0]);
		
		// 将用户名数组和密码数组循环向前移动一位
		SendMailProperties.reUserAndPwd();

		return mailSender;

	}

	protected void doSendMail(String from, String to, String subject, String text) {
		JavaMailSender mailSender = this.getMailSender();

		try {

			SimpleMailMessage message = new SimpleMailMessage();

			message.setFrom(this.prop.getFrom(from));// 发送者.
			message.setTo(to);// 接收者.
			message.setSubject(subject);// 邮件主题.
			message.setText(text);// 邮件内容.

			mailSender.send(message);// 发送邮件

			log.info(LogUtil.format("发送邮件 to:%s title:%s", to, subject));
		} catch (Exception e) {
			log.info(LogUtil.format("发送邮件失败from:%s to:%s title:%s", from, to, subject));
			if (log.isDebugEnabled()) {
				LogUtil.traceError(log, e);
			}
		}
	}

}
