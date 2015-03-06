package com.ctriposs.tsdb.storage;

import com.google.common.base.Preconditions;

public class FileName {

	public static String logFileName(long number){
		return makeFileName(number, "log");
	}
	
	public static String nameFileName(long number){
		return makeFileName(number, "name");
	}
	
	public static String dataFileName(long number, int level){
		return makeFileName(number, level+"-dat");
	}
	
	private static String makeFileName(long number, String suffix){
		Preconditions.checkArgument(number >=0 , "number is negative!");
		Preconditions.checkNotNull(suffix, "suffix is null!");
		return String.format("%06d.%s", number, suffix);
	}
}
