package com.github.shansun.jvm.classloading;

/**
 * 被动使用类字段case01：<br>
 * 通过子类引用父类的静态字段，不会导致子类初始化
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-2
 */
public class SuperClass {
	static {
		System.out.println("SuperClass init!");
	}

	public static int	value	= 123;
}
