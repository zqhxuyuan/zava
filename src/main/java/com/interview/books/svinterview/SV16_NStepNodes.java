package com.interview.books.svinterview;

import com.interview.leetcode.utils.GraphNode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午5:03
 */
public class SV16_NStepNodes {
    public int getNodes(GraphNode n1, GraphNode n2, int n){
        if(n1 == null || n2 == null || n <= 0) return 0;
        int count = 0;
        Queue<GraphNode> queue = new LinkedList<GraphNode>();
        queue.offer(n2);
        while(queue.size() > 0 && n > 0){
            int currentLayerSize = queue.size();
            for(int i = 0; i < currentLayerSize; i++){
                GraphNode node = queue.poll();
                for(GraphNode neighbor : node.neighbors){
                    if(n == 1){  //the last layer, no need to populate it's neighbors.
                        if(neighbor == n2) count++;
                    } else {
                        queue.offer(neighbor);
                    }
                }
            }
            n--;
        }
        return count;
    }
}
