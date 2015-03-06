package com.interview.basics.model.graph.weighted;

import com.interview.basics.model.graph.WeightedGraph;

import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/15/14
 * Time: 11:34 AM
 *
 * Skeleton:
 *  BELLMAN-FORD(G, w, s)   //O(EV)
 *      INITIALIZE-SINGLE-SOURCE(G, s) //对每个顶点初始化 ,O(V)
 *      for i ← 1 to |V[G]| - 1
 *          do for each edge (u, v) ∈ E[G]
 *              do RELAX(u, v, w) //针对每个顶点(V-1 个),都运用松弛技术 O(E),计为 O((v-1)*E))
 *      for each edge (u, v) ∈ E[G]
 *          do if d[v] > d[u] + w(u, v)
 *              then return FALSE   //检测图中每条边,判断是否包含负权回路, 若 d[v]>d[u]+w(u,v),则表示包含,返回 FALSE,
 *      return TRUE //不包含负权回路,返回 TRUE
 */
public class ShortestPath_BellmanFord {
    WeightedGraph g;
    double[] distTo;
    WeightedGraph.Edge[] edgeTo;

    public ShortestPath_BellmanFord(WeightedGraph g) {
        this.g = g;
        init();
    }

    public void init() {
        distTo = new double[g.V];
        edgeTo = new WeightedGraph.Edge[g.V];
    }

    private void relax(WeightedGraph.Edge e) {
        if(distTo[e.t] > distTo[e.s] + e.w){
            distTo[e.t] = distTo[e.s] + e.w;
            edgeTo[e.t] = edgeTo[e.s];
        }
    }

    public boolean solve(int s) {
        distTo[s] = 0.0;
        for(int i = 0; i < g.V; i++){
            if(i != s) distTo[i] = Double.POSITIVE_INFINITY;
        }
        for(int i = 0; i < g.V; i++){
            for(WeightedGraph.Edge edge : g.edges()) relax(edge);
        }
        for(WeightedGraph.Edge edge : g.edges()){
            if(distTo[edge.t] > distTo[edge.s] + edge.w) return false;
        }
        return true;
    }

    public double distTo(int v) {
        return distTo[v];
    }

    public boolean hasPathTo(int v) {
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    public Iterable<WeightedGraph.Edge> pathTo(int s, int v) {
        if (!hasPathTo(v)) return null;
        Stack<WeightedGraph.Edge> path = new Stack<>();
        for (WeightedGraph.Edge edge = edgeTo[v]; edge != null && edge.s != s; edge = edgeTo[edge.s]) {
            path.push(edge);
        }
        return path;
    }
}
