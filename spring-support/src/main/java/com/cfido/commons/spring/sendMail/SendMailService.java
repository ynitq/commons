package com.cfido.commons.spring.sendMail;

import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.cfido.commons.spring.sendMail.SendMailAccountPool.MailAccount;
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
	private SendMailAccountPool accountPool;

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

	protected void doSendMail(String from, String to, String subject, String text) {
		MailAccount mailAccount = this.accountPool.getMailAccount();
		if (mailAccount == null) {
			log.warn("发送邮件时，发现发送邮箱账号池为空，没有账号可用于发送邮件");
		}

		JavaMailSender mailSender = mailAccount.getSender();
		MimeMessage message = mailSender.createMimeMessage();

		try {

			MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

			helper.setFrom(mailAccount.getFrom(from));// 发送者.
			helper.setTo(to);// 接收者.
			helper.setSubject(subject);// 邮件主题.
			helper.setText(text, true);// 邮件内容,HTML格式发送
			helper.setSentDate(new Date());

			mailSender.send(message);// message

			log.info(LogUtil.format("发送邮件 to:%s title:%s", to, subject));
			mailAccount.incCounter(true);
		} catch (Exception e) {
			mailAccount.incCounter(false);
			log.info(LogUtil.format("发送邮件失败from:%s to:%s title:%s", from, to, subject));
			if (log.isDebugEnabled()) {
				LogUtil.traceError(log, e);
			}
		}
	}

}
