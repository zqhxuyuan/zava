package com.interview.basics.model.graph.generic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdjListGraph extends Graph {

	private Map<Vertex, Set<Vertex>> adj = new HashMap<Vertex, Set<Vertex>>();

    public AdjListGraph(int type) {
        super(type);
    }

	@Override
	public void addEdge(Vertex source, Vertex target) {
		this._addEage(source, target);
		if(this.type == UNDIRECTED){
			this._addEage(target, source);
		}
		this.edgeNum++;
	}
	
	private void _addEage(Vertex source, Vertex target){
		Set<Vertex> bag = adj.get(source);
		if(bag == null){
			bag = new HashSet<Vertex>();
			adj.put(source, bag);
			this.vertexNum++;
		}
		bag.add(target);
	}

	@Override
	public Iterable<Vertex> adj(Vertex v) {
		return adj.get(v);
	}

	@Override
	public Iterable<Vertex> vertexs() {
		return adj.keySet();
	}



}
