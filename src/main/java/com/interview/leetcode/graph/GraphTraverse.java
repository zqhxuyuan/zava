package com.interview.leetcode.graph;

import com.interview.leetcode.utils.GraphNode;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-11-23
 * Time: 下午2:28
 */
public class GraphTraverse {

    public void DFS(GraphNode node) {
        if(node == null) return;
        HashSet<GraphNode> visited = new HashSet<>();

        Stack<GraphNode> stack = new Stack<>();
        stack.add(node);

        //Do DFS
        while(stack.size() > 0){
            GraphNode cur = stack.pop();
            System.out.println(cur.label);
            visited.add(cur);
            for(GraphNode neighbor : cur.neighbors){
                if(!visited.contains(neighbor)) {
                    stack.push(neighbor); //neighbor haven't been visited
                }
            }
        }
    }

    public void BFS(GraphNode node) {
        if(node == null) return;
        HashSet<GraphNode> visited = new HashSet<>();

        Queue<GraphNode> queue = new LinkedList<>();
        queue.offer(node);

        //Do BFS
        while(queue.size() > 0){
            GraphNode cur = queue.poll();
            System.out.println(cur.label);
            visited.add(cur);
            for(GraphNode neighbor : cur.neighbors){
                if(!visited.contains(neighbor)) {
                    queue.offer(neighbor); //neighbor haven't been visited
                }
            }
        }
    }
}
