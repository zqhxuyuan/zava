package com.github.shansun.jvm.memory;

/**
 * VM args: -Xss2M
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-9
 */
public class JVMStackOverFlow_case02 {

	private void dontStop() {
		while (true) {

		}
	}

	public void stackLeakByThread() {
		while (true) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					dontStop();
				}
			}).start();
		}
	}

	public static void main(String[] args) {
		new JVMStackOverFlow_case02().stackLeakByThread();
	}

}
