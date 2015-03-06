package com.interview.books.question300;

import com.interview.leetcode.utils.GraphNode;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-12-16
 * Time: 下午3:29
 */
public class TQ36_BipartiteGraphChecker {
    public boolean canBiPartite(GraphNode node) {
        HashSet<GraphNode> oddLayer = new HashSet<>();
        HashSet<GraphNode> evenLayer = new HashSet<>();

        Queue<GraphNode> queue = new LinkedList<>();
        if(node != null) queue.add(node);
        int layer = 0;
        while(!queue.isEmpty()){
            int layerSize = queue.size();
            HashSet<GraphNode> sameLayer = layer % 2 == 0? evenLayer : oddLayer;
            HashSet<GraphNode> oppLayer = layer % 2 == 0? oddLayer : evenLayer;
            for(int i = 0; i < layerSize; i++){
                GraphNode current = queue.poll();
                if(sameLayer.contains(current)) continue;
                if(oppLayer.contains(current)) return false;
                sameLayer.add(current);
                for(GraphNode neighbor : current.neighbors){
                    if(sameLayer.contains(neighbor)) return false;
                    queue.add(neighbor);
                }
            }
            layer++;
        }
        return true;
    }

    public static void main(String[] args){
        TQ36_BipartiteGraphChecker checker = new TQ36_BipartiteGraphChecker();
        List<GraphNode> nodes = GraphNode.sampleDirectedGraph();
        System.out.println(checker.canBiPartite(nodes.get(1)));

        nodes = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            nodes.add(new GraphNode(i));
        }

        nodes.get(1).neighbors.add(nodes.get(2));
        nodes.get(2).neighbors.add(nodes.get(5));
        nodes.get(2).neighbors.add(nodes.get(3));
        nodes.get(3).neighbors.add(nodes.get(4));
        nodes.get(4).neighbors.add(nodes.get(5));
        nodes.get(5).neighbors.add(nodes.get(4));

        System.out.println(checker.canBiPartite(nodes.get(1)));
    }
}
