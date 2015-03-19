package com.github.shansun.jvm.memory;

/**
 * 引用计数算法的缺陷（Python等使用引用计数算法）
 * 
 * VM args: -XX:+PrintGCDetails
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-12
 */
public class ReferenceCountingGC_case01 {

	private ReferenceCountingGC_case01	instance	= null;

	private byte[]				bigSize		= new byte[2 * 1024 * 1024];

	public static void main(String[] args) {
		ReferenceCountingGC_case01 instA = new ReferenceCountingGC_case01();
		ReferenceCountingGC_case01 instB = new ReferenceCountingGC_case01();
		instA.instance = instB;
		instB.instance = instA;

		instA = null;
		instB = null;

		// 这里instA和instB还是会被回收的，但引用计数算法做不到。所以Java不是使用引用计数算法。
		System.gc();
	}

	public ReferenceCountingGC_case01 getInstance() {
		return instance;
	}

	public byte[] getBigSize() {
		return bigSize;
	}
}
