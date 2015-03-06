package com.interview.algorithms.graph;

import com.interview.basics.model.graph.WeightedGraph;

import java.util.*;

/**
 * You have a tree consisting of N vertices numbered 1 to N.
 *
 * Initially each edge has a value equal to zero. You have to first perform M1 operations and then answer M2 queries.
 * Note you have to first perform all the operations and then answer all queries after all operations have been done.
 *
 * Operations are defined by:
 * A B C D: On the path between nodes numbered A and B increase the value of each edge by 1, except for those edges
 * which occur on the path between C and D. Note that there is an unique path between every pair of nodes ie. we don't consider
 * values on edges for finding the path. All four values given in input will be distinct.
 *
 * Queries are of the following type:
 * E F: Print the sum of values of all the edges on the path between two distinct nodes E and F. Again the path will be unique.
 *
 * Given the total node number and the edges between nodes.
 * Given a set of update operations to update the tree.
 * Need find our the queries result.
 *
 */
public class C6_19_UpdateTree {
    public WeightedGraph graph;

    public C6_19_UpdateTree(WeightedGraph graph) {
        this.graph = graph;
    }

    //use BSF to find the path between s and t
    private Stack<WeightedGraph.Edge> path(int s, int t) {
        boolean[] marked = new boolean[graph.V];
        WeightedGraph.Edge[] edges = new WeightedGraph.Edge[graph.V];
        Queue<Integer> queue = new ArrayDeque<Integer>();
        queue.add(s);
        while (!queue.isEmpty()) {
            int c = queue.poll();
            if (c == t) break;
            if (marked[c]) continue;
            marked[c] = true;
            if (graph.adj[c] != null) {
                for (WeightedGraph.Edge e : graph.adj[c]) {
                    if (!marked[e.t]) {
                        queue.add(e.t);
                        edges[e.t] = e;
                    }
                }
            }
        }
        Stack<WeightedGraph.Edge> path = new Stack<>();
        while (t != s) {
            path.push(edges[t]);
            t = edges[t].s;
        }
        return path;
    }

    //check if the edge is the same as un-directed graph
    private boolean isEdgeEquals(WeightedGraph.Edge e1, WeightedGraph.Edge e2) {
        if ((e1.s == e2.s && e1.t == e2.t) || (e1.s == e2.t && e1.t == e2.s)) return true;
        return false;
    }

    public void update(int s1, int t1, int s2, int t2) {

        List<WeightedGraph.Edge> edges = new ArrayList<>();
        //find the path from s1-t1, and s2-t2
        Stack<WeightedGraph.Edge> path1 = path(s1, t1);
        Stack<WeightedGraph.Edge> path2 = path(s2, t2);

        //remove duplicate edges
        for (WeightedGraph.Edge e1 : path1) {
            boolean findEqauls = false;
            for (WeightedGraph.Edge e2 : path2) {
                if (isEdgeEquals(e1, e2)) {
                    findEqauls = true;
                    break;
                }
            }
            if (!findEqauls) edges.add(e1);
        }

        //for each edge, update it's weight, and it's opposite edge
        for (WeightedGraph.Edge e : edges) {
            e.w++;
            for (WeightedGraph.Edge ve : graph.adj[e.t]) {
                if (ve.t == e.s) {
                    ve.w++;
                    break;
                }
            }
        }

    }

    //find the path, and sum the weight in the edge.
    public int query(int s, int t) {
        Stack<WeightedGraph.Edge> path = path(s, t);
        int sum = 0;
        while (!path.empty()) {
            WeightedGraph.Edge edge = path.pop();
            sum += edge.w;
        }
        return sum;
    }
}
