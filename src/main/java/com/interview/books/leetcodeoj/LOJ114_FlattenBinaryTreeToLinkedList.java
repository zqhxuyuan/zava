package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.TreeNode;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午5:04
 */
public class LOJ114_FlattenBinaryTreeToLinkedList {
    //based on pre-order traversal, and backup left and right child
    TreeNode lastVisited;
    public void flatten(TreeNode root) {
        lastVisited = null;
        flattenNode(root);
    }

    public void flattenNode(TreeNode node){
        if(node == null) return;
        TreeNode left = node.left;
        TreeNode right = node.right;
        if(lastVisited != null){
            lastVisited.left = null;
            lastVisited.right = node;
        }
        lastVisited = node;
        flattenNode(left);
        flattenNode(right);
    }
}
