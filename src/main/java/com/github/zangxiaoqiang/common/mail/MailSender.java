package com.github.zangxiaoqiang.common.mail;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.github.zangxiaoqiang.common.conf.ConfigurationManager;
import com.github.zangxiaoqiang.common.conf.GitConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.smtp.SMTPMessage;

public class MailSender {
	protected static final Logger LOG = LoggerFactory.getLogger(MailSender.class);
	private String[] mailGroup = new String[1];
	private String smtpServer;
	private final static String SMTP_SERVER= "SMTP_SERVER";
	private final static String MAIL_GROUP = "Admin_MAIL";
	private static String DEFAULT_SMTP_SERVER= "10.224.160.15";
	private static String DEFAULT_MAIL_GROUP = "hadoopor@github.com";
	private GitConfiguration mailConfig = ConfigurationManager.getConfigFrom("mailConfig.properties");

	/**
	 * SendMailService
	 */
	public MailSender() {
		smtpServer = mailConfig.getValue(SMTP_SERVER, DEFAULT_SMTP_SERVER);
		mailGroup = mailConfig.getValue(MAIL_GROUP, DEFAULT_MAIL_GROUP).split("[,;]");
	}

	/**
	 * 
	 * @param mailBean
	 */
	public int sendMail(MailBean mailBean) throws IOException,
			MessagingException {

		Properties props = System.getProperties();
		props.put("mail.smtp.host", mailBean.getSMTPHost());
		props.put("mail.smtp.port", mailBean.getSMTPPort()==null?25:mailBean.getSMTPPort());
		if(mailBean.isNeedAuth()){
			props.put("mail.smtp.auth", "true");
		}else { 
			props.put("mail.smtp.auth", "false");
		}
		MailAuth auth = new MailAuth();
		Session session = Session.getInstance(props, auth);
		Transport transport = null;
		try {
			transport = session.getTransport("smtp");
		} catch (NoSuchProviderException e1) {
			e1.printStackTrace();
		}
		MimeMessage mime = new MimeMessage(session);
		Address[] addresses = new Address[1];
		Message msg = null;
		try {
			mime.setSubject(mailBean.getSubject());
			mime.setContent(mailBean.getMsgContent(), mailBean.getMessageContentMimeType());
			msg = new SMTPMessage(mime);
			addresses[0] = new InternetAddress(mailBean.getMailTo());
		} catch (MessagingException e1) {
			e1.printStackTrace();
		}

		try {
			if(mailBean.isNeedAuth()){
				transport.connect(mailBean.getMailFrom(), mailBean.getMailPass());
			}else{
				transport.connect();
			}
			transport.sendMessage(msg, addresses);
			LOG.info("Send Mail To : "+ mailBean.getMailTo() +" Successfully!");
		} catch (AuthenticationFailedException e) {
			LOG.error("Authentication failed for mail sender.");
			return -1;
		} catch (MessagingException e) {
			LOG.error(e.getMessage());
			return -1;
		} finally{
			transport.close();
		}
		return 0;
	}

	/**
	 * @param receiver
	 * @param subject
	 *            subject of this mail
	 * @param content
	 *            the message
	 * */
	public void sendMail(String receiver, String subject, String content) throws IOException,
			MessagingException {
		MailBean mb = MailBean.getDefaultMailBean();
		mb.setSMTPHost(getSMTPHost());
		mb.setSubject(subject);
		mb.setMailTo(receiver);
		mb.setMsgContent(content);
		mb.setAuth(false);
		sendMail(mb);
	}
	
	/**
	 * @param subject
	 *            subject of this mail
	 * @param content
	 *            the message
	 * */
	public void sendMail(String subject, String content) throws IOException,
	MessagingException {
		for (String receiver : getMailGroup()) {
			sendMail(receiver, subject, content);
		}
	}

	public void sendMail(String smtpHost, String port, String fromAdd, String pwd, String toAdd, String subject, String msg) throws IOException,
			MessagingException {
		MailBean mb = MailBean.getDefaultMailBean();
		mb.setSubject(subject);
		mb.setSMTPHost(getSMTPHost());
		mb.setSMTPPort(port);
		mb.setMailFrom(fromAdd);
		mb.setMailPass(pwd);
		mb.setMailTo(toAdd);
		mb.setMsgContent(msg);
		mb.setAuth(true);
		sendMail(mb);
	}

	private String getSMTPHost(){
		return smtpServer;
	}

	private String[] getMailGroup() {
		return mailGroup;
	}
}