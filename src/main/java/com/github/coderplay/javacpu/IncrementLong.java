package com.github.coderplay.javacpu;

public class IncrementLong {
	public static final int RUNS = 10;

	public static void main(String[] args) throws Exception {
		Thread.sleep(5000);
		long warmer = 0;
		while (warmer < 500000000L) {
			warmer++;
		}

		long start = System.nanoTime();
		for (int i = 0; i < RUNS; i++) {
			long value = 0;
			while (value < 500000000L) {
				value++;
			}
		}
		long avgTime = (System.nanoTime() - start) / RUNS;
		System.out.println("Cost time: " + avgTime);

	}
}
