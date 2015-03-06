package com.interview.algorithms.graph;

import com.interview.basics.model.graph.generic.AdjListGraph;
import com.interview.basics.model.graph.generic.Graph;
import com.interview.basics.model.graph.generic.Vertex;

import java.util.HashSet;

/**
 * Created_By: zouzhile
 * Date: 2/20/14
 * Time: 5:49 PM
 *
 * Print all the cycles in a directed graph
 */
public class C6_2_GraphCyclePrinter {

    public void printGraphCycles(Graph graph) {
        HashSet<Vertex> visited = new HashSet<Vertex>();
        HashSet<Vertex> candidates = new HashSet<Vertex>();
        for(Vertex vertex : graph.vertexs()){
            if(! visited.contains(vertex) && !candidates.contains(vertex))
                DFS(graph, vertex, visited, candidates);
        }
    }

    public void DFS(Graph graph, Vertex vertex, HashSet<Vertex> visited, HashSet<Vertex> candidates) {
       /*
        1 ---] 2
        ]     /] \
        |    / |  ]
        |   /  |   3
        |  /   |  /
        | [    | [
        5 ----] 4
         */
        candidates.add(vertex);
        for(Vertex adj : graph.adj(vertex)) {

            if(candidates.contains(adj)) {
                // visited nodes may need to revisit to build the cycle
                // so don't put visited.contains(adj) on the if condition.
                // an example is node 4
                // found cycle
                printCycle(vertex, adj);
            } else {
                adj.setParent(vertex); // build the trace back
                DFS(graph, adj, visited, candidates);
            }
        }
        candidates.remove(vertex);
        visited.add(vertex);
    }

    private void printCycle(Vertex from, Vertex to) {
        System.out.print("cycle: ");
        Vertex vertex = from;
        while(vertex != to) {
            System.out.print(vertex.getValue() + " ") ;
            vertex = vertex.getParent();
        }
        System.out.println(to.getValue());
    }



    public static void main(String[] args) {
        Graph graph = generateSampleGraph(Graph.DIRECTED);
        C6_2_GraphCyclePrinter collector = new C6_2_GraphCyclePrinter();
        collector.printGraphCycles(graph);
    }

    public static Graph generateSampleGraph(int type){
        /*
        1 ---] 2
        ]     /] \
        |    / |  ]
        |   /  |   3
        |  /   |  /
        | [    | [
        5 ----] 4
         */
        Graph graph = new AdjListGraph(type);

        Vertex one = new Vertex(1);
        Vertex two = new Vertex(2);
        Vertex three = new Vertex(3);
        Vertex four = new Vertex(4);
        Vertex five = new Vertex(5);

        graph.addEdge(one, two);
        graph.addEdge(two, five);
        graph.addEdge(two, three);
        graph.addEdge(three, four);
        graph.addEdge(four, two);
        graph.addEdge(five, one);
        graph.addEdge(five, four);

        return graph;
    }
}
