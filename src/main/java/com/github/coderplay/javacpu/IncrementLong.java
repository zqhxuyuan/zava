package com.github.coderplay.javacpu;

// long的自增
public class IncrementLong {

    //跑10次,取平均值. 而不是跑一次就得出结论
	public static final int RUNS = 10;

	public static void main(String[] args) throws Exception {
		Thread.sleep(5000);
        //热身运动
		long warmer = 0;
		while (warmer < 500000000L) {
			warmer++;
		}

        //准备开始测试了
		long start = System.nanoTime();
        //要跑10次
		for (int i = 0; i < RUNS; i++) {
            //每次long类型的value都从0开始
			long value = 0;
            //加到5.....
			while (value < 500000000L) {
				value++;
			}
		}
        //10次总的时间/10次, 就跑一次的平均要花多长时间
		long avgTime = (System.nanoTime() - start) / RUNS;
        //4CPU,12G Memory, Cost 166,283,852ns
		System.out.println("Cost time: " + avgTime);

	}
}
