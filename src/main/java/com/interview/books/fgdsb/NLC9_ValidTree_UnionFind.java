package com.interview.books.fgdsb;

import com.interview.books.question300.TQ2_UnionFind;

/**
 * Created_By: stefanie
 * Date: 15-2-2
 * Time: 上午9:57
 */
public class NLC9_ValidTree_UnionFind {

    public boolean valid(int N, int[][] edges){
        TQ2_UnionFind uf = new TQ2_UnionFind(N);

        for(int i = 0; i < edges.length; i++){
            int p1 = uf.find(edges[i][0]);
            int p2 = uf.find(edges[i][1]);
            if(p1 == p2) return false;
            else uf.union(edges[i][0], edges[i][1]);
        }
        int p = uf.find(0);
        for(int i = 1; i < N; i++){
            if(uf.find(i) != p) return false;
        }
        return true;
    }

    public static void main(String[] args){
        NLC9_ValidTree_UnionFind validator = new NLC9_ValidTree_UnionFind();
        int[][] edges = new int[][]{{0,1}, {0,2}, {2,3}, {2,4}};
        System.out.println(validator.valid(5, edges)); //true

        edges = new int[][]{{0,1}, {1,2}, {0,2}, {2,3}, {2,4}};
        System.out.println(validator.valid(5, edges)); //false

        edges = new int[][]{{0,1}, {2,3}};
        System.out.println(validator.valid(5, edges)); //false
    }
}
