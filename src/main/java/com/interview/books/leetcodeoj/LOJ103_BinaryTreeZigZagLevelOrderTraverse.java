package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午2:27
 */
public class LOJ103_BinaryTreeZigZagLevelOrderTraverse {
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> levels = new ArrayList();
        Queue<TreeNode> queue = new LinkedList();
        if(root != null) queue.offer(root);
        boolean isEven = true;
        while(!queue.isEmpty()){
            int levelSize = queue.size();
            List<Integer> current = new ArrayList();
            for(int i = 0; i < levelSize; i++){
                TreeNode node = queue.poll();
                if(isEven)  current.add(node.val);
                else        current.add(0, node.val);
                if(node.left != null) queue.offer(node.left);
                if(node.right != null) queue.offer(node.right);
            }
            levels.add(current);
            isEven = !isEven;
        }
        return levels;
    }
}
