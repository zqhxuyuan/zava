package com.github.zangxiaoqiang.common.mail;


public class MailBean {
	private String mailTo = null;
	private String mailFrom = null;
	private String smtpHost = null;
	private String smtpPort = null;
	private String messageBasePath = null;
	private String subject;
	private String msgContent;
	private String mailAccount = null;
	private String mailPass = null;
	private String messageContentMimeType;
	private String mailbccTo = null;
	private String mailccTo = null;
	private boolean needAuth = false;

	public static final String DEFAULT_MIME_TYPE = "text/html; charset=UTF-8";

	public static MailBean getDefaultMailBean(){
		MailBean mb = new MailBean();
		mb.init();
		return mb;
	}
	
	private void init() {
		setMessageContentMimeType(DEFAULT_MIME_TYPE);
	}
	
	////////////////////////////////////////////////////////////////
	// Getter and setter
	////////////////////////////////////////////////////////////////
	public String getMailBccTo() {
		return mailbccTo;
	}

	public String getMailCcTo() {
		return mailccTo;
	}

	public String getMailAccount() {
		return mailAccount;
	}

	public void setMailAccount(String mailAccount) {
		this.mailAccount = mailAccount;
	}

	public String getMailTo() {
		return mailTo;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public String getMessageBasePath() {
		return messageBasePath;
	}

	public String getSubject() {
		return subject;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public String getMailPass() {
		return mailPass;
	}

	public String getMessageContentMimeType() {
		return messageContentMimeType;
	}

	public void setMailFrom(String from) {
		mailFrom = from;
	}

	public void setMailPass(String strMailPass) {
		mailPass = strMailPass;
	}

	public void setMailTo(String to) {
		mailTo = to;
	}

	public void setMessageBasePath(String basePath) {
		messageBasePath = basePath;
	}

	public void setMessageContentMimeType(String mimeType) {
		messageContentMimeType = mimeType;
	}

	public void setMsgContent(String content) {
		msgContent = content;
	}

	public void setSMTPHost(String host) {
		smtpHost = host;
	}
	
	public String getSMTPHost() {
		return smtpHost;
	}

	public void setSubject(String sub) {
		subject = sub;
	}

	public void setMailbccTo(String bccto) {
		mailbccTo = bccto;
	}

	public void setMailccTo(String ccto) {
		mailccTo = ccto;
	}

	public void setAuth(boolean needAuth) {
		this.needAuth = needAuth;
	}

	public boolean isNeedAuth() {
		return needAuth;
	}

	public void setSMTPPort(String p) {
		smtpPort = p;
	}
	
	public String getSMTPPort() {
		return smtpPort;
	}
}
