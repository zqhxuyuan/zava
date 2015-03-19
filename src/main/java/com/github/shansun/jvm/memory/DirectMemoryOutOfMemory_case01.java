package com.github.shansun.jvm.memory;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * VM args: -Xmx20M -XX:MaxDirectMemorySize=10M
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-9
 */
public class DirectMemoryOutOfMemory_case01 {

	private static final int	_1MB	= 1024 * 1024;

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		Field unsafeField = Unsafe.class.getDeclaredFields()[0];
		unsafeField.setAccessible(true);
		Unsafe unsafe = (Unsafe) unsafeField.get(null);
		while (true) {
			unsafe.allocateMemory(_1MB);
		}
	}

}
