package com.interview.algorithms.dp;

import com.interview.basics.model.graph.WeightedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 14-9-18
 * Time: 下午11:04
 */
public class C12_4_GraphShortestPathPOSNEG {
    static class Result{
        double weight = Integer.MAX_VALUE;
        List<Integer> path = new ArrayList<Integer>();
    }

    public static Result find(WeightedGraph graph, int s, int t){
        double[] opt = new double[graph.V];
        int[] edges = new int[graph.V];
        for(int i = 0; i < graph.V; i++) opt[i] = Integer.MAX_VALUE;
        opt[s] = 0.0;

        Set<Integer> queue = new HashSet<Integer>();
        queue.add(s);
        while(!queue.isEmpty()){
            Integer node = queue.iterator().next();
            queue.remove(node);
            for(WeightedGraph.Edge edge : graph.adj(node)){
                double ten = opt[edge.s] + edge.w;
                if(ten < opt[edge.t]){
                    opt[edge.t] = ten;
                    edges[edge.t] = edge.s;
                    queue.add(edge.t);
                }
            }
        }

        Result result = new Result();
        result.weight = opt[t];
        while(t != s){
            result.path.add(t);
            t = edges[t];
        }
        result.path.add(s);
        return result;
    }
}
