package com.github.zhaoming_mike.concurrent.state;

import java.util.concurrent.TimeUnit;


public class ThreadBlockedDemo {

	public static void main(String[] args) {
		final Object lock1 = new Object();
		final Object lock2 = new Object();

        //线程1请求锁的顺序是lock1, lock2
		final Thread t1 = new Thread() {
			public void run() {
				synchronized (lock1) {
					try {
						TimeUnit.SECONDS.sleep(3);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized (lock2) {
						System.out.println(getName() + " running..");
					}
				}
			}
		};
        //线程2请求锁的顺序是lock2, lock1
		final Thread t2 = new Thread() {
			public void run() {
				synchronized (lock2) {
					synchronized (lock1) {
						System.out.println(getName() + " running..");
					}
				}
			}
		};
		t1.start();
		t2.start();

        /**
         t1:TIMED_WAITING   t1执行到线程1,占用lock1后,休眠了3秒,进入等待状态
         t2:BLOCKED         t2执行到线程2,占用lock2,由于它还需要lock1,但是lock1被t1持有,所以被阻塞住
         t1:TIMED_WAITING   主线程停了1秒后,继续执行t1, 此时线程1才休眠了1秒
         t2:BLOCKED         t2还是被阻塞
         t1:TIMED_WAITING   主线程再停了1秒, 此时线程1休眠了2秒了
         t2:BLOCKED         t2还是被阻塞
         t1:BLOCKED         主线程停了1秒,总共3秒了, 线程1从休眠状态恢复, 要执行接下去的占用lock2锁.但是lock2已被t2持有,所以也阻塞
         t2:BLOCKED         接下去进入死锁
         t1:BLOCKED
         t2:BLOCKED
         */
		new Thread() {
			public void run() {
				while(true) {
					System.out.println("t1:" + t1.getState());
					System.out.println("t2:" + t2.getState());
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
}
