package com.github.shansun.jvm.classloading;

/**
 * 被动引用例子一<br>
 * -XX:+TraceClassLoading
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-2
 */
public class NotInitialization_case01 {

	public static void main(String[] args) {
		/*
		 * 这里引用的SubClass.value其实是在SuperClass中，所以只会初始化SuperClass，不会初始化SubClass;
		 * 但是会记载SubClass
		 */
		System.out.println(SubClass.value);
	}

}
