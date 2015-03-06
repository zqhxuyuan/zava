package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.TreeNode;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-31
 * Time: 下午4:38
 */
public class LOJ173_BSTIterator {
    Stack<TreeNode> nodes;
    public LOJ173_BSTIterator(TreeNode root) {
        nodes = new Stack();
        pushLeft(root);
    }

    private void pushLeft(TreeNode node){
        while(node != null){
            nodes.push(node);
            node = node.left;
        }
    }

    /** @return whether we have a next smallest number */
    public boolean hasNext() {
        return nodes.size() != 0;
    }

    /** @return the next smallest number */
    public int next() {
        TreeNode node = nodes.pop();
        pushLeft(node.right);
        return node.val;
    }
}
