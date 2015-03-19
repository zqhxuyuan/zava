package com.github.shansun.jvm.classloading;

/**
 * 被动引用例子2: <br>
 * 通过数组定义来引用类，不会触发此类的初始化
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-2
 */
public class NotInititalization_case02 {

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		SuperClass[] sca = new SuperClass[20];
	}
}
