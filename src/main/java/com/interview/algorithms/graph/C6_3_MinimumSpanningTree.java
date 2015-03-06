package com.interview.algorithms.graph;

import com.interview.basics.model.graph.generic.Graph;
import com.interview.basics.model.graph.generic.Vertex;

import java.util.HashSet;

/**
 * Created_By: zouzhile
 * Date: 2/21/14
 * Time: 7:23 AM
 */
public class C6_3_MinimumSpanningTree {

    public void generateMST(Graph graph) {
        HashSet<Vertex> visited = new HashSet<Vertex>();
        HashSet<Vertex> candidates = new HashSet<Vertex>();

        for(Vertex vertex : graph.vertexs()) {
            if(! visited.contains(vertex)) {
                candidates.add(vertex);
                while(! candidates.isEmpty()) {
                    Vertex currentVertex = candidates.iterator().next();
                    for(Vertex adj : graph.adj(currentVertex)){
                        if(! candidates.contains(adj) && ! visited.contains(adj)) {
                            adj.setParent(currentVertex);
                            System.out.println(currentVertex.getValue() + " -> " + adj.getValue());
                            candidates.add(adj);
                        }
                    }
                    candidates.remove(currentVertex);
                    visited.add(currentVertex);
                }
            }
        }
    }

}
