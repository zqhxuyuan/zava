package com.interview.books.leetcodeoj;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午5:39
 */
public class LOJ117_PopulateNextPointerII {
    class TreeLinkNode {
        int val;
        TreeLinkNode left, right, next;
    }
    public void connect(TreeLinkNode root) {
        if(root == null) return;
        Queue<TreeLinkNode> queue = new LinkedList();
        queue.offer(root);
        while(!queue.isEmpty()){
            int levelSize = queue.size();
            TreeLinkNode prev = null;
            for(int i = 0; i < levelSize; i++){
                TreeLinkNode node = queue.poll();
                if(prev != null) prev.next = node;
                prev = node;
                if(node.left != null) queue.offer(node.left);
                if(node.right != null) queue.offer(node.right);
            }
        }
    }
}
