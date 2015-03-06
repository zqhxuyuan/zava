package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 14-12-15
 * Time: 上午10:13
 */
public class TQ2_UnionFind {
    int[] unions;
    int[] sizes;

    public TQ2_UnionFind(int N){
        unions = new int[N];
        sizes = new int[N];
        for(int i = 0; i < N; i++){
            unions[i] = i;
            sizes[i] = 1;
        }
    }

    public int find(int node){
        while(unions[node] != node) node = unions[node];
        return node;
    }

    public void union(int node1, int node2){
        int parent1 = find(node1);
        int parent2 = find(node2);
        if(parent1 == parent2) return;
        if(sizes[parent1] >= sizes[parent2]){ //to make more balanced tree
            unions[parent2] = parent1;
            sizes[parent1] += sizes[parent2];
        } else {
            unions[parent1] = parent2;
            sizes[parent2] += sizes[parent1];
        }
    }

    public boolean connected(int node1, int node2){
        return find(node1) == find(node2);
    }

    public static void main(String[] args){
        TQ2_UnionFind uf = new TQ2_UnionFind(10);
        uf.union(1,4);
        uf.union(1,2);
        uf.union(5,7);
        uf.union(7,9);
        uf.union(5,6);

        System.out.println(uf.connected(4,2));//true
        System.out.println(uf.connected(9,6));//true
        System.out.println(uf.connected(9,2));//false
    }
}
