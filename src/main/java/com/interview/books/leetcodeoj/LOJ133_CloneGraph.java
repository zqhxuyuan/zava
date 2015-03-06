package com.interview.books.leetcodeoj;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-12-27
 * Time: 下午12:12
 */
public class LOJ133_CloneGraph {
    class UndirectedGraphNode{
        int label;
        List<UndirectedGraphNode> neighbors;

        public UndirectedGraphNode(int label){
            this.label = label;
            neighbors = new ArrayList();
        }
    }
    //use BFS to clone the graph, keep HashMap<oldNode, cloneNode> pair
    //when clone a new node, put in nodeMap and also offer in queue
    //make sure the node in stack have a copy in nodeMap, use nodeMap as visited
    public UndirectedGraphNode cloneGraph(UndirectedGraphNode node) {
        if(node == null) return null;
        Map<UndirectedGraphNode, UndirectedGraphNode> nodeMap = new HashMap();
        Queue<UndirectedGraphNode> queue = new LinkedList();
        queue.offer(node);
        nodeMap.put(node, new UndirectedGraphNode(node.label));
        while(!queue.isEmpty()){
            UndirectedGraphNode current = queue.poll();
            UndirectedGraphNode clone = nodeMap.get(current);
            for(UndirectedGraphNode neighbor : current.neighbors){
                if(nodeMap.containsKey(neighbor)){
                    clone.neighbors.add(nodeMap.get(neighbor));
                } else {
                    UndirectedGraphNode cloneNeighbor = new UndirectedGraphNode(neighbor.label);
                    nodeMap.put(neighbor, cloneNeighbor);
                    queue.offer(neighbor);
                    clone.neighbors.add(cloneNeighbor);
                }
            }
        }
        return nodeMap.get(node);
    }
}
