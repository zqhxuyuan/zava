package com.interview.algorithms.graph;

import java.util.ArrayList;
import java.util.List;

import com.interview.basics.model.collection.heap.BinaryArrayHeap;
import com.interview.basics.model.collection.heap.Heap;
import com.interview.basics.model.graph.WeightedGraph;
import com.interview.basics.model.graph.WeightedGraph.Edge;

/**
 * Basic Idea: 
 * 		Start with vertex 0 and greedily grow tree T.
 * 		Add to T the min weight edge with exactly one endpoint in T.
 * 		Repeat until V - 1 edges.
 * @author stefanie
 *
 */
public class C6_3_MSTSolverPrim {

    public static List<Edge> getMST(WeightedGraph graph){
        boolean[] visited = new boolean[graph.V];
        Heap<Edge> minHeap = new BinaryArrayHeap<>(Heap.MIN_HEAD);
        List<Edge> MST = new ArrayList<>();
        visit(0, visited, graph, minHeap);
        while(MST.size() < graph.V - 1 && minHeap.size() != 0){
            Edge minEdge = minHeap.pollHead();
            if(visited[minEdge.t]) continue;
            MST.add(minEdge);
            visit(minEdge.t, visited, graph, minHeap);
        }
        return MST;
    }

    private static void visit(int v, boolean[] visited, WeightedGraph graph, Heap<Edge> minHeap){
		visited[v] = true;
		if(graph.adj[v] != null){
			for(Edge e : graph.adj[v]){
				if(!visited[e.t]) minHeap.add(e);
			}
		}
	}

}
