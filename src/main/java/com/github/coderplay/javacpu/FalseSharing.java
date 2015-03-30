package com.github.coderplay.javacpu;

public final class FalseSharing implements Runnable {
	public static int NUM_THREADS = 4; // change 如果你的机器是4核,则运行该任务CPU会100%
	public final static long ITERATIONS = 500L * 1000L * 1000L;
	private final int arrayIndex;
	private static VolatileLong[] longs;

	public FalseSharing(final int arrayIndex) {
		this.arrayIndex = arrayIndex;
	}

	public static void main(final String[] args) throws Exception {
		Thread.sleep(10000);
		System.out.println("starting....");
		if (args.length == 1) {
			NUM_THREADS = Integer.parseInt(args[0]);
		}

        //初始化数组, 其中VolatileLong的value初始是0
		longs = new VolatileLong[NUM_THREADS];
		for (int i = 0; i < longs.length; i++) {
			longs[i] = new VolatileLong();
		}

		final long start = System.nanoTime();
		runTest();
		System.out.println("duration = " + (System.nanoTime() - start));

        for(VolatileLong v : longs){
            System.out.println(v.value); //1
        }
	}

	private static void runTest() throws InterruptedException {
		Thread[] threads = new Thread[NUM_THREADS];

		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new FalseSharing(i));
		}

		for (Thread t : threads) {
			t.start();
		}

		for (Thread t : threads) {
			t.join();
		}
	}

    @Override
	public void run() {
		long i = ITERATIONS + 1;
		while (0 != --i) {
			longs[arrayIndex].value = i;
		}
	}

	public static long preventFromOptimization(VolatileLong v) {
		return v.p1 + v.p2 + v.p3 + v.p4 + v.p5 + v.p6;
	}

	public final static class VolatileLong {
		public volatile long value = 0L;
		public long p1, p2, p3, p4, p5, p6; // comment out
	}
}