package com.github.shansun.jvm.memory;

import java.util.ArrayList;
import java.util.List;

/**
 * 毕玄的GC示例
 * 
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-2
 */
public class GCDemo {
	public static void main(String[] args) throws Exception {
		System.out.println("ready to start");
		Thread.sleep(10000);
		List<GCDataObject> oldGenObjects = new ArrayList<GCDataObject>();
		for (int i = 0; i < 51200; i++) {
			oldGenObjects.add(new GCDataObject(2));
		}
		System.gc();
		oldGenObjects.size();
		oldGenObjects = null;
		Thread.sleep(5000);
		List<GCDataObject> tmpObjects = new ArrayList<GCDataObject>();
		for (int i = 0; i < 3200; i++) {
			tmpObjects.add(new GCDataObject(5));
		}
		tmpObjects.size();
		tmpObjects = null;
	}
}

class GCDataObject {
	byte[]		bytes	= null;
	RefObject	object	= null;

	public GCDataObject(int factor) {
		// create object in kb
		bytes = new byte[factor * 1024];
		object = new RefObject();
	}
}

class RefObject {
	RefChildObject	object;

	public RefObject() {
		object = new RefChildObject();
	}
}

class RefChildObject {
	public RefChildObject() {
		;
	}
}
