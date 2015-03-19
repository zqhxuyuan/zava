package com.github.shansun.jvm.memory;

import java.util.ArrayList;
import java.util.List;

/**
 * VM args: -XX:PermSize=10M -XX:MaxPermSize=10M
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-9
 */
public class RuntimeConstantPoolOutOfMemory_case01 {

	public static void main(String[] args) {
		// 使用List保存着常量池的引用，避免FullGC回收常量池行为
		List<String> list = new ArrayList<String>();

		// 10MB的永久代在Integer范围内足够产生OOM了
		int i = 0;
		while (true) {
			list.add(String.valueOf(i++).intern());
		}
	}

}
