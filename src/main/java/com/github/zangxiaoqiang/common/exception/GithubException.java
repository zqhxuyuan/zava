package com.github.zangxiaoqiang.common.exception;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class GithubException extends RuntimeException {
	private static final long serialVersionUID = 6198150057805620142L;

	private static ResourceBundle rb = ResourceBundle.getBundle("errorCode");

	private String errorCode;
	private String message;
	private Throwable cause = this;

	public GithubException(String errorCode) {
		this.errorCode = errorCode;
		init(null);
	}

	public GithubException(String errorCode, Object... parameters) {
		this.errorCode = errorCode;
		init(parameters);
	}

	public GithubException(String errorCode, Throwable cause) {
		this.errorCode = errorCode;
		this.cause = cause;
		init(null);
	}

	public GithubException(String errorCode, Throwable cause,
			Object... parameters) {
		this.errorCode = errorCode;
		this.cause = cause;
		init(parameters);
	}

	private void init(Object[] parameters) {
		if (errorCode == null) {
			message = "unknown exception";
			return;
		}
		message = rb.getString(errorCode);
		message = MessageFormat.format(message, parameters);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getCause() {
		return (cause == this ? null : cause);
	}
}
