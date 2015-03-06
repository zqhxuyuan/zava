package com.interview.books.question300;

import com.interview.leetcode.utils.WeightedGraphNode;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created_By: stefanie
 * Date: 14-12-16
 * Time: 下午2:25
 */
public class TQ31_MinimumSpanningTree {
    static Comparator<WeightedGraphNode.Edge> comparator = new Comparator<WeightedGraphNode.Edge>() {
        @Override
        public int compare(WeightedGraphNode.Edge o1, WeightedGraphNode.Edge o2) {
            if(o1.weight == o2.weight) return 0;
            else if(o1.weight < o2.weight) return -1;
            else return 1;
        }
    };

    public void prim(WeightedGraphNode node){
        PriorityQueue<WeightedGraphNode.Edge> heap = new PriorityQueue<>(10, comparator);
        HashSet<WeightedGraphNode> visited = new HashSet<>();
        visited.add(node);
        for(WeightedGraphNode.Edge edge : node.edges) heap.add(edge);
        int total = 0;
        while(!heap.isEmpty()){
            WeightedGraphNode.Edge edge = heap.poll();
            if(visited.contains(edge.tar)) continue;
            visited.add(edge.tar);
            System.out.println(edge.src.label + " --" + edge.weight + "--> " + edge.tar.label);
            total += edge.weight;
            for(WeightedGraphNode.Edge next : edge.tar.edges) {
                if(!visited.contains(next.tar)) heap.add(next);
            }
        }
        System.out.println("Total weight: " + total);
    }

    //kruskal can't use on directed graph, may generate a MST which nodes can't reachable.
    public void kruskal(List<WeightedGraphNode> nodes){
        PriorityQueue<WeightedGraphNode.Edge> heap = new PriorityQueue<>(10, comparator);
        for(WeightedGraphNode node : nodes){
            for(WeightedGraphNode.Edge edge : node.edges) heap.add(edge);
        }
        TQ2_UnionFind uf = new TQ2_UnionFind(nodes.size());
        int total = 0;
        while(!heap.isEmpty()){
            WeightedGraphNode.Edge edge = heap.poll();
            if(uf.connected(edge.src.label, edge.tar.label)) continue;
            System.out.println(edge.src.label + " --" + edge.weight + "--> " + edge.tar.label);
            uf.union(edge.src.label, edge.tar.label);
            total += edge.weight;
        }
        System.out.println("Total weight: " + total);
    }

    public static void main(String[] args){
        TQ31_MinimumSpanningTree mst = new TQ31_MinimumSpanningTree();
        List<WeightedGraphNode> nodes = WeightedGraphNode.sampleDirectedGraph();
        mst.prim(nodes.get(1));
        mst.kruskal(nodes);
    }
}
