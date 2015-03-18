package com.shansun.sparrow.command;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-5-4
 */
public class CommandException extends Exception {

	private static final long	serialVersionUID	= -1005255280946481429L;

	private String				errorCode;

    public CommandException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

	public CommandException(String errorCode, String errorMessage, Throwable e) {
		super(errorMessage, e);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
