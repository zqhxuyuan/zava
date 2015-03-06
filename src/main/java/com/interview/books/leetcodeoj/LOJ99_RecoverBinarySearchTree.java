package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.TreeNode;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午2:12
 */
public class LOJ99_RecoverBinarySearchTree {
    //find the breakpoint during in-order traverse by checking last visited node
    //swap the values of breakpoint node
    TreeNode first;
    TreeNode second;
    TreeNode last;
    public void recoverTree(TreeNode root) {
        first = null;
        second = null;
        last = null;
        findBreakpoint(root);
        Integer tmp = first.val;
        first.val = second.val;
        second.val = tmp;
    }

    public void findBreakpoint(TreeNode node){
        if(node == null) return;
        findBreakpoint(node.left);
        if(last != null && node.val < last.val){
            if(first == null) first = last;
            second = node;
        }
        last = node;
        findBreakpoint(node.right);
    }
}
