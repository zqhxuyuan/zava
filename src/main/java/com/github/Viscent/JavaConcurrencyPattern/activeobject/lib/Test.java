package com.github.Viscent.JavaConcurrencyPattern.activeobject.lib;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Test {

	public static void main(String[] args) throws InterruptedException,
	    ExecutionException {

		SampleActiveObject sao = ActiveObjectProxy.newInstance(
		    SampleActiveObject.class, new SampleActiveObjectImpl(),
		    Executors.newCachedThreadPool());
		Future<String> ft = null;
		try {
			ft = sao.process("Something", 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.sleep(500);
		System.out.println(ft.get());
	}
}
