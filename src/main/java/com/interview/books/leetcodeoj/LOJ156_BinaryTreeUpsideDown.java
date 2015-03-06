package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.TreeNode;

/**
 * Created_By: stefanie
 * Date: 14-12-30
 * Time: 下午8:33
 */
public class LOJ156_BinaryTreeUpsideDown {
    //tracking parent and parentRight, as reverse linked list do while to reverse the root.left and right
    public TreeNode UpsideDownBinaryTree(TreeNode root) {
        TreeNode parent = null;
        TreeNode parentRight = null;
        while(root != null){
            TreeNode next = root.left;
            root.left = parentRight;
            parentRight = root.right;
            root.right = parent;
            parent = root;
            root = next;
        }
        return parent;
    }
}
