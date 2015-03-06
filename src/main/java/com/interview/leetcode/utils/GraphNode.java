package com.interview.leetcode.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-23
 * Time: 下午1:40
 */
public class GraphNode {
    public int label;
    public List<GraphNode> neighbors;

    public GraphNode(int x) {
        label = x;
        neighbors = new ArrayList<GraphNode>();
    }

    public static List<GraphNode> sampleDirectedGraph(){
         /*
        1 ---] 2
        ]     /] \
        |    / |  ]
        |   /  |   3
        |  /   |  /
        | [    | [
        5 ----] 4
         */
        List<GraphNode> nodes = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            nodes.add(new GraphNode(i));
        }

        nodes.get(1).neighbors.add(nodes.get(2));
        nodes.get(2).neighbors.add(nodes.get(5));
        nodes.get(2).neighbors.add(nodes.get(3));
        nodes.get(3).neighbors.add(nodes.get(4));
        nodes.get(4).neighbors.add(nodes.get(2));
        nodes.get(5).neighbors.add(nodes.get(4));
        nodes.get(5).neighbors.add(nodes.get(1));

        return nodes;
    }
}
