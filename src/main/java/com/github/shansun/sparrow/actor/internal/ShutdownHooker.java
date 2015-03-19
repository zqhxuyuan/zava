package com.github.shansun.sparrow.actor.internal;

/**
 * JVM退出钩子
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-25
 */
public class ShutdownHooker {

	public static void init() {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				
			}
		});
	}

	public static void main(String[] args) {
		System.out.println("Hello Main");
		System.exit(0);
	}

}
