package com.github.zangxiaoqiang.common.string;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.zangxiaoqiang.common.exception.ErrorCode;
import com.github.zangxiaoqiang.common.exception.GithubException;


public class StringValidator {
	public static void verifyEmpty(String paraName, String parameter) {
		if (parameter == null || parameter.trim().isEmpty()) {
			throw new GithubException(ErrorCode.COMMON_EMPTY_STRING, paraName);
		}
	}

	public static void verifyNotNull(String paraName, String parameter) {
		if (parameter == null || parameter.isEmpty()) {
			throw new GithubException(ErrorCode.COMMON_EMPTY_STRING, paraName);
		}
	}

	public static void verifyLength(String paraName, String parameter, int lengthLimit) {
		if (parameter != null && parameter.length() > lengthLimit) {
			throw new GithubException(ErrorCode.COMMON_LENGTH_EXCEED, paraName, lengthLimit);
		}
	}

	public static void verifyPattern(String paraName, String parameter,
			String pattern) {
		if (parameter != null && !parameter.matches(pattern)) {
			throw new GithubException(ErrorCode.COMMON_ILLEGAL_STRING,
					paraName, parameter, pattern);
		}
	}
	
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	public static Date verifyDate(String date) {
		return verifyDate(date, DEFAULT_DATE_FORMAT);
	}

	public static Date verifyDate(String date, String dateFormat) {
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		if (date == null || date.isEmpty()) {
			throw new GithubException(ErrorCode.COMMON_EMPTY_STRING, "date");
		}
		try {
			format.setLenient(false);
			return format.parse(date);
		} catch (ParseException e) {
			throw new GithubException(ErrorCode.COMMON_DATEFORMAT_ILLEGAL,
					date, dateFormat);
		}
	}
}
