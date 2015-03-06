package com.interview.books.question300;

import com.interview.leetcode.utils.GraphNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-16
 * Time: 下午1:54
 */
public class TQ30_CyclePrinter {
    public void print(List<GraphNode> nodes){
        HashSet<GraphNode> visited = new HashSet<>();
        for(GraphNode node : nodes){
            if(visited.contains(node)) continue;
            List<GraphNode> path = new ArrayList<>();
            HashMap<GraphNode, Integer> indexes = new HashMap();
            dfs(node, path, indexes, visited);
        }
    }

    private void dfs(GraphNode node, List<GraphNode> path, HashMap<GraphNode, Integer> indexes, HashSet<GraphNode> visited) {
        if(indexes.containsKey(node)){ //found a cycle
            int index = indexes.get(node);
            while(index < path.size()) System.out.print(path.get(index++).label + ", ");
            System.out.println(node.label);
            return;
        }
        visited.add(node);
        indexes.put(node, path.size());
        path.add(node);
        for(GraphNode neighbor : node.neighbors){
            dfs(neighbor, path, indexes, visited);
        }
        path.remove(node);
        indexes.remove(node);
    }

    public static void main(String[] args){
        List<GraphNode> nodes = GraphNode.sampleDirectedGraph();
        TQ30_CyclePrinter printer = new TQ30_CyclePrinter();
        printer.print(nodes);
    }
}
