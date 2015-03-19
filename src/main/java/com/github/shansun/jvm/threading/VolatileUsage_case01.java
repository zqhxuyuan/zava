package com.github.shansun.jvm.threading;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-12
 */
public class VolatileUsage_case01 {

	public static  int	race	= 0;

	public static void increase() {
		race++;
	}

	private static final int	THREADS_COUNT	= 100;

	public static void main(String[] args) {
		Thread[] threads = new Thread[THREADS_COUNT];
		for (int i = 0; i < THREADS_COUNT; i++) {
			threads[i] = new Thread(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < 100000; i++) {
						increase();
					}
				}
			});

			threads[i].start();
		}

		// 等待累加线程都结束
		while (Thread.activeCount() > 1) {
			Thread.yield(); // 暂停当前线程，并执行其他线程
		}

		// 输出结果不是50000，说明volatile非并发安全
		System.err.println(race);
	}

}
