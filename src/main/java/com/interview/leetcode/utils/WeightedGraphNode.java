package com.interview.leetcode.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-16
 * Time: 下午2:26
 */



public class WeightedGraphNode {
    public static class Edge {
        public WeightedGraphNode src;
        public WeightedGraphNode tar;
        public float weight;

        public Edge(WeightedGraphNode src, WeightedGraphNode tar, float weight) {
            this.src = src;
            this.tar = tar;
            this.weight = weight;
        }
    }

    public int label;
    public List<Edge> edges;

    public WeightedGraphNode(int x) {
        label = x;
        edges = new ArrayList<>();
    }

    public static List<WeightedGraphNode> sampleDirectedGraph(){
         /*
           1
        1 ---] 2
        ]     /] \ 2
        |  1 / |  ]
      4 |   / 2|   3
        |  /   |  /
        | [    | [ 4
        5 ----] 4
           4
         */
        List<WeightedGraphNode> nodes = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            nodes.add(new WeightedGraphNode(i));
        }

        nodes.get(1).edges.add(new Edge(nodes.get(1), nodes.get(2), 1));
        nodes.get(2).edges.add(new Edge(nodes.get(2), nodes.get(5), 2));
        nodes.get(2).edges.add(new Edge(nodes.get(2), nodes.get(3), 2));
        nodes.get(3).edges.add(new Edge(nodes.get(3), nodes.get(4), 4));
        nodes.get(4).edges.add(new Edge(nodes.get(4), nodes.get(2), 2));
        nodes.get(5).edges.add(new Edge(nodes.get(5), nodes.get(4), 4));
        nodes.get(5).edges.add(new Edge(nodes.get(5), nodes.get(1), 4));

        return nodes;
    }
}
