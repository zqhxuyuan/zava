package com.interview.basics.model.graph.searcher;

public interface Processor {
	public void preProcess(int v);
	
	public void postProcess(int v);
}
