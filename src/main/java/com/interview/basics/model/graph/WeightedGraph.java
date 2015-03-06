package com.interview.basics.model.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeightedGraph {
	public class Edge implements Comparable<Edge>{
		public int s; 
		public int t;
		public double w;
		
		public Edge(int s, int t, double w){
			this.s = s;
			this.t = t;
			this.w = w;
		}

		@Override
		public int compareTo(Edge e) {
			if(this.w < e.w) 		return -1;
			else if(this.w > e.w) 	return 1;
			else 					return 0;
		}
		
		public void print(){
			System.out.printf("%d-%d(%.2f)\t", this.s, this.t, this.w);
		}
	}
	
	public double weight = 0;
	public boolean ordered;
	public int V;
	public Set<Edge>[] adj;
	
	@SuppressWarnings("unchecked")
	public WeightedGraph(int size, boolean ordered){
		this.V = size;
		this.ordered = ordered;
		adj = (Set<Edge>[]) new Set[V];
	}
	
	public void addDirectEdge(int v, int w, double weight){
		if(adj[v] == null){
			adj[v] = new HashSet<Edge>();
		}
		Edge edge = new Edge(v, w, weight);
		adj[v].add(edge);
	}
	
	public void addEdge(int v, int w, double weight){
		addDirectEdge(v, w, weight);
		if(!ordered)
			addDirectEdge(w, v, weight);
		this.weight += weight;
	}
	
	public void addEdge(Edge edge){
		addEdge(edge.s, edge.t, edge.w);
	}
	
	public Iterable<Edge> adj(int v){
		return adj[v]; 
	}
	
	public double weight(){
		return this.weight;
	}
	
	public List<Edge> edges(){
		List<Edge> edges = new ArrayList<Edge>();
		for(int i = 0; i < V; i++){
			if(adj[i] == null)	continue;
			for(Edge edge: adj(i)){
				edges.add(edge);
			}
		}
		return edges;
	}
	
	public void print(){
		for(int i = 0; i < V; i++){
			System.out.print(i + "\t");
			if(adj(i) != null){
				for(Edge edge : adj(i)){
					edge.print();
				}
			}
			System.out.println();
		}
	}
	
	public Iterable<Edge> getSortedEdge(boolean isAsc){
		List<Edge> edges = edges();
		if(isAsc){
			Collections.sort(edges);
		} else {
			Collections.sort(edges, Collections.reverseOrder());
		}
		return edges;
	}
}
