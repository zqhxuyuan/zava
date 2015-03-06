package com.interview.algorithms.graph;

import com.interview.basics.model.collection.heap.BinaryArrayHeap;
import com.interview.basics.model.collection.heap.Heap;
import com.interview.basics.model.graph.WeightedGraph;
import com.interview.basics.model.graph.WeightedGraph.Edge;

import com.interview.algorithms.general.C1_3_UnionFind;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Basic Idea: 
 *   1. sort the edge by weight in ascending order
 *   2. add next edge to tree T unless doing so would create a cycle.
 * Time: O(E log E)
 * 	If edges are already sorted, order of growth is E log* V.
 * 
 * Pf. Kruskal's algorithm is a special case of the greedy MST algorithm.
 * 	Suppose Kruskal's algorithm colors the edge e = v–w black.
 * 	Cut = set of vertices connected to v in tree T.
 * 	No crossing edge is black.	
 * 	No crossing edge has lower weight.
 * @author stefanie
 *
 */
public class C6_3_MSTSolverKruskal {

	public static List<Edge> getMST(WeightedGraph graph) {
        List<Edge> MST = new ArrayList<Edge>();
        Heap<Edge> minHeap = new BinaryArrayHeap<Edge>(Heap.MIN_HEAD);
        for (Edge edge : graph.edges()) minHeap.add(edge);

        C1_3_UnionFind uf = new C1_3_UnionFind(graph.V);
        while (MST.size() < graph.V - 1 && minHeap.size() != 0) {
            Edge minEdge = minHeap.pollHead();
            if (uf.connected(minEdge.s, minEdge.t)) continue;
            uf.union(minEdge.s, minEdge.t);
            MST.add(minEdge);
        }
        return MST;
    }
}
