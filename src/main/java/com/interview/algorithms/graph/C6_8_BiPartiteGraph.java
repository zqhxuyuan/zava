package com.interview.algorithms.graph;

import java.util.HashSet;
import java.util.Set;

import com.interview.basics.model.graph.Graph;
import com.interview.basics.model.graph.searcher.ProblemSolver;

public abstract class C6_8_BiPartiteGraph extends ProblemSolver {
	protected boolean[] flags;
	
	public C6_8_BiPartiteGraph(Graph g){
		super(g);
		flags = new boolean[g.V];
	}
	
	public boolean isBiPartite(){
		for(int w = 0; w < g.V; w++){
			if(g.adj[w] != null){
				for(int v : g.adj[w]){
					if(flags[w] == flags[v])
						return false;
				}
			}
		}
		return true;
	}
	
	public Set<Integer>[] getBiPartite(){
		@SuppressWarnings("unchecked")
		Set<Integer>[] partition = (Set<Integer>[]) new Set[2];
		for(int i = 0; i < g.V; i++){
			int index = flags[i]? 1: 0;
			Set<Integer> set = partition[index];
			if(set == null){
				set = new HashSet<Integer>();
				partition[index] = set;
			}
			set.add(i);
		}
		return partition;
	}
	
	@Override
	public void preProcess(int v) {
		
	}

	@Override
	public void postProcess(int v) {
		// TODO Auto-generated method stub
		
	}
}
