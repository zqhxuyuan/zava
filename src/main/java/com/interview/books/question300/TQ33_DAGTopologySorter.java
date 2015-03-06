package com.interview.books.question300;

import com.interview.leetcode.utils.GraphNode;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-12-16
 * Time: 下午3:04
 */
public class TQ33_DAGTopologySorter {

    public List<GraphNode> sort(List<GraphNode> nodes){
        List<GraphNode> sorted = new ArrayList<GraphNode>();
        HashMap<GraphNode, Integer> indegree = new HashMap<>();
        for(GraphNode node : nodes){
            for(GraphNode neighbor : node.neighbors){
                if(indegree.containsKey(neighbor)) indegree.put(neighbor, indegree.get(neighbor) + 1);
                else indegree.put(neighbor, 1);
            }
        }
        //put 0-in-degree node in queue
        Queue<GraphNode> queue = new LinkedList();
        for(GraphNode node : nodes){
            if(!indegree.containsKey(node)) {
                queue.offer(node);
            }
        }

        while(!queue.isEmpty()){
            GraphNode node = queue.poll();
            sorted.add(node);
            for(GraphNode neighbor : node.neighbors){
                indegree.put(neighbor, indegree.get(neighbor) - 1);
                if(indegree.get(neighbor) == 0) queue.offer(neighbor);
            }
        }

        return sorted;
    }

    public static void main(String[] args){
        TQ33_DAGTopologySorter sorter = new TQ33_DAGTopologySorter();
        List<GraphNode> nodes = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            nodes.add(new GraphNode(i));
        }

        nodes.get(1).neighbors.add(nodes.get(2));
        nodes.get(1).neighbors.add(nodes.get(4));
        nodes.get(2).neighbors.add(nodes.get(3));
        nodes.get(2).neighbors.add(nodes.get(5));
        nodes.get(3).neighbors.add(nodes.get(4));

        List<GraphNode> sorted = sorter.sort(nodes);
        for(int i = 0; i < sorted.size(); i++){
            System.out.print(sorted.get(i).label + ", ");
        }
        //0, 1, 2, 3, 5, 4,
    }
}
