package com.interview.algorithms.graph;

import com.interview.basics.model.graph.generic.AdjListGraph;
import com.interview.basics.model.graph.generic.Graph;
import com.interview.basics.model.graph.generic.Vertex;

import java.util.HashMap;
import java.util.Map;

/**
 * Problem: Is a graph bipartite?
 * @author chenting
 *
 */
public class C6_8_BiPartiteGenericGraph {
	public static final Integer UNVISITED = 0;
	
	private Map<Vertex, Integer> flag = new HashMap<Vertex, Integer>();
	
	public boolean bipartite(Graph g){
		boolean isSuccess = true;
		for(Vertex vertex : g.vertexs()){
			if(flag.get(vertex) == null){
				isSuccess = isSuccess && dfs(g, vertex, 1);
				if(!isSuccess) return isSuccess;
			}
		}
		return true;
	}
	
	public boolean dfs(Graph g, Vertex v, int previousFlag){
		int currentFlag = 0;
		if(previousFlag == 1){
			currentFlag = 2;
		} else {
			currentFlag = 1;
		}
		flag.put(v, currentFlag);
		for(Vertex w: g.adj(v)){
			if(flag.get(w) == null){
				dfs(g, w, currentFlag);
			} else if(flag.get(w) != previousFlag){
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args){
		Graph g = new AdjListGraph(Graph.UNDIRECTED);
		g.type = Graph.UNDIRECTED;
		g.addEdge(new Vertex(0), new Vertex(1));
		g.addEdge(new Vertex(0), new Vertex(5));
		g.addEdge(new Vertex(1), new Vertex(3));
		g.addEdge(new Vertex(2), new Vertex(3));
		g.addEdge(new Vertex(6), new Vertex(4));
		g.addEdge(new Vertex(4), new Vertex(5));
		g.addEdge(new Vertex(2), new Vertex(4));
		g.addEdge(new Vertex(0), new Vertex(2));
		g.addEdge(new Vertex(0), new Vertex(6));
		
		
		C6_8_BiPartiteGenericGraph biGraph = new C6_8_BiPartiteGenericGraph();
		boolean isBiPartition = biGraph.bipartite(g);
		System.out.println("Is Bi-Partition?\t" + isBiPartition);
		
		
		for(Map.Entry<Vertex, Integer> item : biGraph.flag.entrySet()){
			System.out.println(item.getKey().getValue() + "\t" + item.getValue());
		}
	}
}
