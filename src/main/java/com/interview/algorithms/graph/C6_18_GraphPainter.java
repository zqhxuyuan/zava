package com.interview.algorithms.graph;

import com.interview.basics.model.graph.Graph;

/**
 * Created_By: stefanie
 * Date: 14-10-5
 * Time: 下午4:17
 *
 * The solution only find a print color solution, not the minimal solution.
 */
public class C6_18_GraphPainter {
    public static int[] paint(Graph graph){
        int[] color = new int[graph.V];

        for(int i = 0; i < graph.V; i++){
            if(graph.adj(i) == null) color[i] = 1;
        }

        int i = 0;
        while(i < graph.V){
            if(color[i] == 1);
            else if(color[i] == 0){
                color[i] = 1;
                for(Integer t : graph.adj(i)) {
                    color[t] = 2;
                }
            } else {
                for(Integer t: graph.adj(i)){
                    if(color[t] == color[i]) color[t]++;
                }
            }
            i++;
        }
        return color;
    }

}
