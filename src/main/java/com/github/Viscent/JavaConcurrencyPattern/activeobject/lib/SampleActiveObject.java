package com.github.Viscent.JavaConcurrencyPattern.activeobject.lib;

import java.util.concurrent.Future;

public interface SampleActiveObject {
	public Future<String> process(String arg, int i);
}