package com.interview.algorithms.dp;

import com.interview.basics.model.graph.searcher.IndexedPriorityQueue;

/**
 * Created by stefanie on 2014/6/26.
 *
 * Given an undirected graph G having positive weights and N vertices.
 * You start with having a sum of M money. For passing through a vertex i, you must pay S[i] money.
 * If you don't have enough money - you can't pass through that vertex. Find the shortest path from vertex 1 to vertex N,
 * respecting the above conditions; or state that such path doesn't exist. If there exist more than one path having the same length,
 * then output the cheapest one. Restrictions: 1<N<=100 ; 0<=M<=100 ; for each i, 0<=S[i]<=100.
 */
public class C12_11_ShortestPaidPath {

    static class Result{
        int weight = Integer.MAX_VALUE;
        int left = 0;
    }

    public static Result find(int[][] graph, int[] S, int M, int T){
        int N = S.length;

        boolean[] states = new boolean[N];
        int[][] Min = new int[N][M + 1];
        for(int i = 0; i < N; i++){
            for(int j = 0; j <= M; j++){
                Min[i][j] = Integer.MAX_VALUE;
            }
        }
        Min[0][M] =0;

        IndexedPriorityQueue<String, Integer> queue = new IndexedPriorityQueue<String, Integer>();
        queue.add(0 + "-" + M,0);

        while(!queue.isEmpty()){
            String state = queue.poll();
            int k = Integer.parseInt(state.split("-")[0]);
            int l = Integer.parseInt(state.split("-")[1]);
            if(!states[k]){
                for(int p = 0; p < N; p++){
                    if(graph[k][p] != 0){
                        // If (l-S[p]>=0 AND Min[p][l-S[p]]>Min[k][l]+graph[k][p]) Then Min[p][l-S[p]]=Min[k][l]+graph[k][p]
                        int left = l-S[p];
                        if( left >= 0 && Min[p][left] > Min[k][l] + graph[k][p]){
                            Min[p][left] = Min[k][l] + graph[k][p];
                            queue.add(p + "-" + left, Min[p][left]);
                            states[k] = true;
                        }
                    }
                }
            }
        }
        Result r = new Result();
        for(int j = M; j >= 0; j--){
            if(Min[T][j] < r.weight){
                r.weight = Min[T][j];
                r.left = j;
            }
        }
        return r;
    }
}
