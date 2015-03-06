package com.interview.basics.model.graph;

import java.util.HashSet;
import java.util.Set;

public class Graph {
	public boolean ordered;
	public int V;
	public Set<Integer>[] adj;
	
	@SuppressWarnings("unchecked")
	public Graph(int size, boolean ordered){
		this.V = size;
		this.ordered = ordered;
		adj = (Set<Integer>[]) new Set[V];
	}
	
	public void addDirectEdge(int v, int w){
		if(adj[v] == null){
			adj[v] = new HashSet<Integer>();
		}
		adj[v].add(w);
	}
	
	public void addEdge(int v, int w){
		addDirectEdge(v, w);
		if(!ordered)
			addDirectEdge(w, v);
	}
	
	public Iterable<Integer> adj(int v){
		return adj[v]; 
	}
	
	public void print(){
		for(int i = 0; i < V; i++){
			System.out.print(i + "\t");
			if(adj(i) != null){
				for(int j : adj(i)){
					System.out.print(j + " ");
				}
			}
			System.out.println();
		}
	}
}
