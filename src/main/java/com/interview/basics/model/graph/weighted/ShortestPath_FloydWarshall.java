package com.interview.basics.model.graph.weighted;

import com.interview.basics.model.graph.WeightedGraph;

/**
 * Created_By: stefanie
 * Date: 15-1-14
 * Time: 下午9:54
 */

/**
 * FLOYD-WARSHALL: O(N^3)
 *  for (k = 1 to n)
 *      for (i = 1 to n)
 *          for (j = 1 to n)
 *              adj[i][j] = min(adj[i][j], adj[i][k] + adj[k][j]);
 */
public class ShortestPath_FloydWarshall {

    public double[][] shortestPath(WeightedGraph graph){
        double[][] paths = new double[graph.V][graph.V];
        for(int i = 0; i < graph.V; i++){
            for(WeightedGraph.Edge edge : graph.adj(i)){
                paths[edge.s][edge.t] = edge.w;
            }
        }

        for(int i = 0; i < graph.V; i++){
            for(int j = 0; j < graph.V; j++){
                for(int k = 0; k < graph.V; k++){
                    if (paths[i][k] + paths[k][j] < paths[i][j])
                        paths[i][j] = paths[i][k] + paths[k][j];
                }
            }
        }
        return paths;
    }
}
