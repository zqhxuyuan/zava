package com.interview.basics.model.graph.generic;

public abstract class Graph {
	public static int UNDIRECTED = 1;
	public static int DIRECTED = 2;
	
	public int type;
	public int vertexNum;
	public int edgeNum;

    public Graph() {
        this.type = UNDIRECTED;
    }

    public Graph(int type) {
        this.type = UNDIRECTED;
        if(type == DIRECTED)
            this.type = type;
    }
	public int V(){
		return vertexNum;
	}

	public int E(){
		return edgeNum;
	}

    public Vertex getVertex(int value){
        for(Vertex v : this.vertexs())
            if((Integer)v.value == value)
                return v;
        return null;
    }
	
	public abstract void addEdge(Vertex source, Vertex target);
	
	public abstract Iterable<Vertex> adj(Vertex v);
	
	public abstract Iterable<Vertex> vertexs();
	
}
