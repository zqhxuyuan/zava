package com.interview.books.svinterview;
import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午12:35
 */
public class SV8_CommonAncestorGenericTree {
    class GenericTreeNode implements Comparable<GenericTreeNode>{
        int val;
        List<GenericTreeNode> children;

        @Override
        public int compareTo(GenericTreeNode o) {
            if(o == null) return 1;
            else return this.val - o.val;
        }
    }

    //do the BFS save the parents of each node until found both n1 and n2.
    //backtrace the first common nodes in the path
    public GenericTreeNode LCA(GenericTreeNode root, GenericTreeNode n1, GenericTreeNode n2){
        if(root == null || n1 == null || n2 == null) return null;
        HashMap<GenericTreeNode, GenericTreeNode> parents = new HashMap<>();

        Queue<GenericTreeNode> queue = new LinkedList();
        queue.offer(root);
        boolean foundN1 = false;
        boolean foundN2 = false;
        while(!queue.isEmpty()){
            GenericTreeNode node = queue.poll();
            if(node == n1)  foundN1 = true;
            else if(node == n2) foundN2 = true;
            if(foundN1 && foundN2) break;
            for(GenericTreeNode child : node.children){
                parents.put(child, node);
                queue.add(child);
            }
        }
        if(foundN1 == false || foundN2 == false) return null;
        Stack<GenericTreeNode> pathN1 = getPath(n1, parents);
        Stack<GenericTreeNode> pathN2 = getPath(n2, parents);

        GenericTreeNode common = null;
        while(!pathN1.isEmpty() && !pathN2.isEmpty()){
            GenericTreeNode p1 = pathN1.pop();
            GenericTreeNode p2 = pathN2.pop();
            if(p1 == p2) common = p1;
            else break;
        }
        return common;
    }

    public Stack<GenericTreeNode> getPath(GenericTreeNode node, HashMap<GenericTreeNode, GenericTreeNode> parents){
        Stack<GenericTreeNode> path = new Stack<>();
        path.add(node);
        if(parents.containsKey(node)){
            path.add(parents.get(node));
        }
        return path;
    }
}
