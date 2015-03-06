package com.github.coderplay.javacpu;

public class IncrementLongWithSynchronized {

	private static long value;

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
	}
}
