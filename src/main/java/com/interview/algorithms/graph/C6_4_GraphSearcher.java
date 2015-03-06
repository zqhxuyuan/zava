package com.interview.algorithms.graph;

import com.interview.basics.model.graph.generic.AdjListGraph;
import com.interview.basics.model.graph.generic.Graph;
import com.interview.basics.model.graph.generic.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created_By: zouzhile
 * Date: 9/23/13
 * Time: 5:05 PM
 */

public class C6_4_GraphSearcher {

    public Graph generateSampleGraph(int type){
        /*
        1 ---- 2
        |     /| \
        |    / |  \
        |   /  |   3
        |  /   |  /
        | /    | /
        5 ----- 4
         */
        Graph graph = new AdjListGraph(type);

        Vertex one = new Vertex(1);
        Vertex two = new Vertex(2);
        Vertex three = new Vertex(3);
        Vertex four = new Vertex(4);
        Vertex five = new Vertex(5);

        graph.addEdge(one, two);
        graph.addEdge(one, five);
        graph.addEdge(two, five);
        graph.addEdge(two, four);
        graph.addEdge(two, three);
        graph.addEdge(three, four);
        graph.addEdge(four, five);
        return graph;
    }

    // do depth first search
    public List<Vertex> DFS(Graph graph) {
        List<Vertex> sequence = new ArrayList<Vertex>();
        HashSet<Vertex> candidates = new HashSet<Vertex>();
        HashSet<Vertex> visited = new HashSet<Vertex>();
        for(Vertex vertex : graph.vertexs())
                DFS(graph, vertex, candidates, visited, sequence);
        return sequence;
    }

    public void DFS(Graph graph, Vertex vertex, HashSet<Vertex> candidates, HashSet<Vertex> visited, List<Vertex> sequence) {
        if(candidates.contains(vertex) || visited.contains(vertex))
            return;
        candidates.add(vertex);
        for(Vertex adj : graph.adj(vertex))
            if(! visited.contains(adj) && ! candidates.contains(adj))
                DFS(graph, adj, candidates, visited, sequence);
        candidates.remove(vertex);
        sequence.add(vertex);
        visited.add(vertex);
    }

    public List<Vertex> BFS(Graph graph) {
        List<Vertex> globalSequence = new ArrayList<Vertex>();
        HashSet<Vertex> queue = new HashSet<Vertex>();
        HashSet<Vertex> visited  = new HashSet<Vertex> ();
        for(Vertex vertex : graph.vertexs()) {
            List<Vertex> localSequence = this.BFS(graph, vertex, queue, visited);
            globalSequence.addAll(localSequence);
        }
        return globalSequence;
    }

    private List<Vertex> BFS(Graph graph, Vertex source, HashSet<Vertex> candidates, HashSet<Vertex> visited) {
        List<Vertex> localSequence = new ArrayList<Vertex>();
        if(! visited.contains(source)) {
            // Do BFS from current vertex
            candidates.add(source);
            while(!candidates.isEmpty()) {
                // the new unvisited vertexes reachable from current level of vertexes
                HashSet<Vertex> newCandidates = new HashSet<Vertex>();
                // visit the current level of vertexes
                for(Vertex candidate : candidates) {
                    if(! visited.contains(candidate)) {
                        // add all the unvisited adjacent vertexes of current candidate to new candidates
                        for(Vertex adj : graph.adj(candidate))
                            if(!visited.contains(adj) && ! candidates.contains(adj))
                                newCandidates.add(adj);
                        localSequence.add(candidate); // the logic of "visit" current candidate
                        visited.add(candidate);
                    }
                }
                candidates.clear();
                candidates.addAll(newCandidates);
            }
        }
        return localSequence;
    }

    public List<Vertex> BFS(Graph graph, Vertex source) {
        return this.BFS(graph, source, new HashSet<Vertex>(), new HashSet<Vertex>());
    }

    public static void main(String[] args) {
        C6_4_GraphSearcher searcher = new C6_4_GraphSearcher();

        System.out.println("Undirected Graph - Breadth First Search");
        Graph graph = searcher.generateSampleGraph(Graph.UNDIRECTED);
        for(Vertex vertex : searcher.BFS(graph, graph.getVertex(1)))
            System.out.println(vertex.getValue() + " ");

        System.out.println();

        System.out.println("Undirected Graph - Depth First Search");
        graph = searcher.generateSampleGraph(Graph.UNDIRECTED);
        for(Vertex vertex : searcher.DFS(graph))
            System.out.println(vertex.getValue() + " ");
    }

}
