package com.github.coderplay.javacpu;

public class L1CacheMiss {
	private static final int RUNS = 10;
	private static final int DIMENSION_1 = 1024 * 1024;
	private static final int DIMENSION_2 = 62;

	private static double[][] longs;

	public static void main(String[] args) throws Exception {
		Thread.sleep(10000);
		longs = new double[DIMENSION_1][];
		for (int i = 0; i < DIMENSION_1; i++) {
			longs[i] = new double[DIMENSION_2];
			for (int j = 0; j < DIMENSION_2; j++) {
				longs[i][j] = 0.0;
			}
		}
		System.out.println("starting....");

		final long start = System.nanoTime();
		double sum = 0.0;
		for (int r = 0; r < RUNS; r++) {
            //duration:11,849,368,737
//			for (int j = 0; j < DIMENSION_2; j++) {
//				for (int i = 0; i < DIMENSION_1; i++) {
//					sum += longs[i][j];
//				}
//			}

            //duration:778,471,971
			for (int i = 0; i < DIMENSION_1; i++) {
				for (int j = 0; j < DIMENSION_2; j++) {
					sum += longs[i][j];
				}
			}
		}
		System.out.println("duration = " + (System.nanoTime() - start));
		System.out.println(sum);
	}
}