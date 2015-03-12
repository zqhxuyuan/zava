package com.ctriposs.sdb.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
	
	public static String formatCurrentDate() {
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sDate = formatter.format(new Date());
		return sDate;
	}

}
