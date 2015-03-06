package com.interview.basics.model.graph.weighted;

import com.interview.basics.model.graph.WeightedGraph;
import com.interview.basics.model.graph.WeightedGraph.Edge;
import com.interview.basics.model.graph.searcher.IndexedPriorityQueue;

import java.util.Stack;

/**
 * Dijkstra Algorithm: Dynamic Programming
 * Can represent the SPT with two vertex-indexed arrays:
 * distTo[v] is length of shortest path from s to v.
 * edgeTo[v] is last edge on shortest path from s to v.
 * <p/>
 * dist(s, t) = min(dist(s, t') + weight(t', t))
 *
 * @author stefanie
 *
 * skeleton:
 *      DIJKSTRA(G, w, s)  //O((V+E)logE)
 *         INITIALIZE-SINGLE-SOURCE(G, s)
 *         S←Ø
 *         Q ← V[G]   //V*O(1)
 *         while Q ≠ Ø
 *              do u ← EXTRACT-MIN(Q)    //EXTRACT-MIN,V*O(V),V*O(lgV)
 *              S ← S ∪{u}
 *              for each vertex v ∈ Adj[u]
 *                  do RELAX(u, v, w)    //松弛技术,E*O(1),E*O(lgV)。
 */
public class ShortestPath_Dijkstra {
    WeightedGraph g;
    double[] distTo;
    Edge[] edgeTo;
    IndexedPriorityQueue<Integer, Double> pq;


    public ShortestPath_Dijkstra(WeightedGraph g) {
        this.g = g;
        init();
    }

    public void init() {
        distTo = new double[g.V];
        edgeTo = new Edge[g.V];
        pq = new IndexedPriorityQueue<Integer, Double>();
        for (int i = 0; i < g.V; i++) {
            distTo[i] = Double.POSITIVE_INFINITY;
        }
    }

    public void solve(int s) {
        distTo[s] = 0.0;
        pq.add(s, 0.0);
        while (!pq.isEmpty()) {
            int u = pq.poll();
            if (g.adj[u] != null) {
                for (Edge e : g.adj[u]) relax(e);
            }
        }
    }

    private void relax(Edge e) {
        if (distTo[e.t] > distTo[e.s] + e.w) {
            distTo[e.t] = distTo[e.s] + e.w;
            edgeTo[e.t] = e;
            if (pq.contains(e.t)) pq.update(e.t, distTo[e.t]);
            else pq.add(e.t, distTo[e.t]);
        }
    }

    public double distTo(int v) {
        return distTo[v];
    }

    public boolean hasPathTo(int v) {
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    public Iterable<Edge> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        Stack<Edge> path = new Stack<Edge>();
        for (Edge edge = edgeTo[v]; edge != null; edge = edgeTo[edge.s]) {
            path.push(edge);
        }
        return path;
    }
}
