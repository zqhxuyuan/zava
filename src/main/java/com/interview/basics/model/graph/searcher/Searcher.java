package com.interview.basics.model.graph.searcher;


import com.interview.basics.model.graph.Graph;

public abstract class Searcher {
	protected Graph g;
	protected boolean[] marked;
	protected int[] edges;
	boolean isBreak = false;
	
	public Searcher(Graph g){
		this.g = g;
	}
	
	public abstract void search(int s, Processor p);
	
	public void init(){
		marked = new boolean[g.V];
		edges = new int[g.V];
		for(int i = 0; i < g.V; i++){
			edges[i] = -1;
		}
	}
	
	public String path(int s, int v){
		search(s, null);
		if(marked[v]){
			StringBuilder builder = new StringBuilder();
			builder.append(v);
			int n = edges[v];
			while(n != s){
				builder.append("-" + n);
				n = edges[n];
			}
			builder.append("-" + s);
			return builder.toString();
		} else {
			return "";
		}
	}
	
	public boolean isMarked(int s){
		return marked[s];
	}
	
	public int getPrevious(int s){
		return edges[s];
	}
	
	public void cleanPath(){
		for(int i = 0; i < g.V; i++){
			edges[i] = -1;
		}
	}
	
	public void cleanMark(){
		marked = new boolean[g.V];
	}
	
	public boolean allMarked(){
		for(int i = 0; i < g.V; i++){
			if(marked[i] == false) 	return false;
		}
		return true;
	}

	public boolean isBreak() {
		return isBreak;
	}

	public void setBreak(boolean isBreak) {
		this.isBreak = isBreak;
	}
	
	
}
