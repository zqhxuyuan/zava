package com.interview.basics.model.graph;

/**
 * Created_By: stefanie
 * Date: 15-1-14
 * Time: 下午9:56
 */
public class MatrixGraph {
    boolean ordered = true;
    int V;
    int[][] adj;

    public MatrixGraph(int vertexNumber, boolean ordered){
        this.ordered = ordered;
        this.V = vertexNumber;
        adj = new int[vertexNumber][vertexNumber];
    }

    public MatrixGraph(int[][] graph, boolean ordered){
        this.ordered = ordered;
        this.V = graph.length;
        adj = graph;
    }

    public void addDirectEdge(int v, int w, int weight){
        adj[v][w] = weight;
    }

    public void addEdge(int v, int w, int weight){
        addDirectEdge(v, w, weight);
        if(!ordered)
            addDirectEdge(w, v, weight);
    }

    public int[] adj(int v){
        return adj[v];
    }

    public int getWeight(int v, int w){
        return adj[v][w];
    }

    public void print(){
        for(int i = 0; i < V; i++){
            System.out.print(i + "\t");
            if(adj(i) != null){
                for(int j : adj(i)){
                    System.out.print(j + " ");
                }
            }
            System.out.println();
        }
    }
}
