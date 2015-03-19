package com.github.shansun.jvm.btrace;

import java.util.Random;

/**
 * 使用Btrace的实验目标代码
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-12
 */
public class BTraceTargetDemo {

	public static void main(String[] args) throws InterruptedException {
		Random random = new Random();
		CaseObject object = new CaseObject();
		boolean result = true;

		while (result) {
			result = object.execute(random.nextInt(1000));
			Thread.sleep(3000);
		}
	}
}
