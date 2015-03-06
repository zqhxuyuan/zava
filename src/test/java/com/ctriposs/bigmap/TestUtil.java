package com.ctriposs.bigmap;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class TestUtil {
	
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static Random rnd = new Random();
	
	private static final NumberFormat MEM_FMT = new DecimalFormat("##,###.##");

	public static String randomString(int len ) 
	{
	   StringBuilder sb = new StringBuilder( len );
	   for( int i = 0; i < len; i++ ) 
	      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	   return sb.toString();
	}
	
	public static void sleepQuietly(long duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			// ignore
		}
	}
	
	public static final String TEST_BASE_DIR = "d:/bigmap_test/";
	
	public static String kbString(long memBytes) {
		return MEM_FMT.format(memBytes / 1024) + " kb";
    }
	
	public static String printMemoryFootprint() {
      Runtime run = Runtime.getRuntime();
      String memoryInfo = "Memory - free: " + kbString(run.freeMemory()) + " - max:" + kbString(run.maxMemory()) + "- total:" + kbString(run.totalMemory());
      return memoryInfo;
	}
}
