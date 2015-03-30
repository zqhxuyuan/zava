package com.github.coderplay.javacpu;

public class IncrementLongWithVolatile {

	private static volatile long value;

	public static void increment() {
		while (value < 500000000L) {
			value++;
		}
	}

	public static void main(String[] args) throws Exception {
		Thread.sleep(5000);
		value = 0;
		increment();

		value = 0;
		long start = System.nanoTime();
		increment();
		System.out.println("Cost time: " + (System.nanoTime() - start));
        //3,154,953,320
	}
}
