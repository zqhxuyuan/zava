package com.github.shansun.jvm.memory;

import java.util.ArrayList;
import java.util.List;

/**
 * VM args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-6-9
 */
public class HeapOutOfMemory_case01 {

	static class OOMObject {

	}

	public static void main(String[] args) {
		List<OOMObject> list = new ArrayList<OOMObject>();

		while (true) {
			list.add(new OOMObject());
		}
	}

}
