package com.github.shansun.jvm.btrace;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-12
 */
public class CaseObject {
	private static int	sleepTotalTime	= 0;

	public boolean execute(int sleepTime) throws InterruptedException {
		System.err.println("Sleep: " + sleepTime);
		sleepTotalTime += sleepTime;
		Thread.sleep(sleepTime);
		return true;
	}
}
