package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.TreeNode;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午9:02
 */
public class LOJ124_BinaryTreeMaximumPathSum {
    //do pre-order traversal, maxPath = left + right + node.val;
    //return max singlePath: max(max(left, right) + node.val, 0)
    int max;
    public int maxPathSum(TreeNode root) {
        max = Integer.MIN_VALUE;
        visitTree(root);
        return max;
    }

    public int visitTree(TreeNode node){
        if(node == null) return 0;
        int left = visitTree(node.left);
        int right = visitTree(node.right);
        max = Math.max(max, left + right + node.val);
        return Math.max(Math.max(left, right) + node.val, 0);
    }
}
