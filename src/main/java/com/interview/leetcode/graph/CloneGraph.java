package com.interview.leetcode.graph;

import com.interview.leetcode.utils.GraphNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-11-23
 * Time: 下午1:40
 */
public class CloneGraph {

    public GraphNode cloneGraphDFS(GraphNode node) {
        if(node == null) return null;
        HashMap<GraphNode, GraphNode> nodeMap = new HashMap<>();

        Stack<GraphNode> stack = new Stack<GraphNode>();
        stack.add(node);
        nodeMap.put(node, new GraphNode(node.label));  //make sure the node in stack have a copy in nodeMap

        //Do DFS
        while(stack.size() > 0){
            GraphNode cur = stack.pop();
            GraphNode clone = nodeMap.get(cur);
            for(GraphNode neighbor : cur.neighbors){
                if(nodeMap.containsKey(neighbor)){
                    clone.neighbors.add(nodeMap.get(neighbor));
                } else {
                    stack.push(neighbor); //neighbor haven't been visited
                    GraphNode cloneneighbor = new GraphNode(neighbor.label);
                    nodeMap.put(neighbor, cloneneighbor);
                    clone.neighbors.add(cloneneighbor);
                }
            }
        }
        return nodeMap.get(node);
    }

    public GraphNode cloneGraphBFS(GraphNode node) {
        if(node == null) return null;
        HashMap<GraphNode, GraphNode> nodeMap = new HashMap<>();

        Queue<GraphNode> queue = new LinkedList<GraphNode>();
        queue.add(node);
        nodeMap.put(node, new GraphNode(node.label));  //make sure the node in stack have a copy in nodeMap

        //Do DFS
        while(queue.size() > 0){
            GraphNode cur = queue.poll();
            GraphNode clone = nodeMap.get(cur);
            for(GraphNode neighbor : cur.neighbors){
                if(nodeMap.containsKey(neighbor)){
                    clone.neighbors.add(nodeMap.get(neighbor));
                } else {
                    queue.offer(neighbor); //neighbor haven't been visited
                    GraphNode cloneneighbor = new GraphNode(neighbor.label);
                    nodeMap.put(neighbor, cloneneighbor);
                    clone.neighbors.add(cloneneighbor);
                }
            }
        }
        return nodeMap.get(node);
    }



    //Recursively
    public GraphNode cloneGraphDFSRecursively(GraphNode node) {
        if(node == null) return null;
        HashMap<GraphNode, GraphNode> nodeMap = new HashMap<>();
        return cloneDFS(node, nodeMap);
    }

    public GraphNode cloneDFS(GraphNode node, HashMap<GraphNode, GraphNode> nodeMap){
        if(nodeMap.containsKey(node)) return nodeMap.get(node); //already cloned
        GraphNode clone = new GraphNode(node.label);
        nodeMap.put(node, clone);
        for(GraphNode neighbor : node.neighbors){
            clone.neighbors.add(cloneDFS(neighbor, nodeMap));
        }
        return clone;
    }

}
