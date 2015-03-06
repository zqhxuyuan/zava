package com.interview.flag.f;

import com.interview.leetcode.utils.TreeNode;

/**
 * Created_By: stefanie
 * Date: 15-1-1
 * Time: 下午4:30
 */
public class F7_MaxSubtree {
    TreeNode maxRoot;
    int max;
    public TreeNode maxSubtree(TreeNode root){
        maxRoot = null;
        max = Integer.MIN_VALUE;
        visit(root);
        return maxRoot;
    }

    public int visit(TreeNode node){
        if(node == null) return 0;
        int left = visit(node.left);
        int right = visit(node.right);
        int sum = left + right + node.val;
        if(sum > max){
            max = sum;
            maxRoot = node;
        }
        return sum;
    }
}
