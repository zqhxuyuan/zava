package com.interview.basics.model.graph.generic.weighted;

/**
 * Created_By: zouzhile
 * Date: 3/16/14
 * Time: 9:34 AM
 */
public class Edge {
    private Vertex source;
    private Vertex target;
    int weight = 1;

    public Edge(Vertex source, Vertex target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public Vertex getSource(){
        return this.source;
    }

    public Vertex getTarget(){
        return this.target;
    }

    public int getWeight() {
        return this.weight;
    }

}
