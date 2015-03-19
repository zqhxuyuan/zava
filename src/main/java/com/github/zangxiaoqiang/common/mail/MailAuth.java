package com.github.zangxiaoqiang.common.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;



public class MailAuth extends Authenticator {
	private static String MAIL_USER = "admin@github.com";
	private static String MAIL_PASSWORD = "";

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(MAIL_USER, MAIL_PASSWORD);
	}

	public static String getAuthUser() {
		return MAIL_USER;
	}

	public static void setAuthUser(String user) {
		MAIL_USER = user;
	}

	public static String getAuthPwd() {
		return MAIL_PASSWORD;
	}

	public static void setAuthPwd(String pwd) {
		MAIL_PASSWORD = pwd;
	}
	
}