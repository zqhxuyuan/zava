package com.github.shansun.jvm.memory;

/**
 * VM args: -Xss128k
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-9
 */
public class JVMStackOverFlow_case01 {

	private static int	stackLength	= 1;

	static void stackLeak() {
		stackLength++;

		stackLeak();
	}

	public static void main(String[] args) throws Throwable {
		try {
			stackLeak();
		} catch (Throwable e) {
			System.err.println("stack length: " + stackLength);
			throw e;
		}
	}

}
