package com.interview.books.svinterview;

import com.interview.leetcode.utils.TreeNode;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午12:12
 */
public class SV7_BSTCommonAncestor {

    public TreeNode LCA(TreeNode root, TreeNode n1, TreeNode n2){
        if(root == null || n1 == null || n2 == null) return null;
        //both n1 and n2 value is smaller than root
        if(root.val > n1.val && root.val > n2.val) return LCA(root.left, n1, n2);
        //both n1 and n2 value is larger than root
        else if(root.val < n1.val && root.val < n2.val) return LCA(root.right, n1, n2);
        else return root;
    }
}
