package com.github.zhaoming_mike.concurrent.communication;

import java.util.concurrent.CountDownLatch;

/**
 * 倒计时触发器Demo
 * @author zhaoming@yy.com
 *
 */
public class CountDownLatchDemo {

	static final int countDownSize = 5;

	public static void main(String[] args) throws InterruptedException {
		//初始化一个指定大小的Latch
        CountDownLatch latch = new CountDownLatch(countDownSize);

        //将Latch分别传给Waiter和Decrementer
		Waiter waiter = new Waiter(latch);
		Decrementer decrementer = new Decrementer(latch);

        //启动线程
        new Thread(waiter).start();
		new Thread(decrementer).start();
		Thread.sleep(6000);
	}
}

/**
 * 等待
 */
class Waiter implements Runnable {
	CountDownLatch latch = null;

	public Waiter(CountDownLatch latch) {
		this.latch = latch;
	}

	public void run() {
		try {
            //直到latch减为0时,会触发这里,才执行后面的代码
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Waiter Released");
	}
}

/**
 * 递减操作
 */
class Decrementer implements Runnable {
	CountDownLatch latch = null;

	public Decrementer(CountDownLatch latch) {
		this.latch = latch;
	}

	public void run() {
		try {
			for (int i = 1; i <= CountDownLatchDemo.countDownSize; i++) {
				Thread.sleep(1000);
				this.latch.countDown();
				System.out.println(this + " run " + i + " times.");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
