package com.interview.algorithms.graph;

import com.interview.basics.model.graph.searcher.DFSearcher;
import com.interview.basics.model.graph.Graph;

public class C6_8_BiPartiteGraphDFS extends C6_8_BiPartiteGraph {
	
	public C6_8_BiPartiteGraphDFS(Graph g){
		super(g);
		searcher = new DFSearcher(g);
	}

	@Override
	public void preProcess(int v) {
		int p = searcher.getPrevious(v);
		if(p >= 0){
			flags[v] = !flags[p];
		} else {
			flags[v] = true;
		}
	}
}
