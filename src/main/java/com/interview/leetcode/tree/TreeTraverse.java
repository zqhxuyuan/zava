package com.interview.leetcode.tree;

import com.interview.leetcode.utils.TreeNode;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-11-22
 * Time: 下午1:56
 */
public class TreeTraverse {

    /**
     * Given a binary tree, return the preorder traversal of its nodes' values.
     * put right/left child in stack
     */
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> nodes = new ArrayList<Integer>();
        Stack<TreeNode> stack = new Stack<TreeNode>();
        if(root != null) stack.push(root);
        while(stack.size() > 0){
            TreeNode node = stack.pop();
            nodes.add(node.val);
            if(node.right != null) stack.push(node.right);
            if(node.left != null) stack.push(node.left);
        }
        return nodes;
    }

    /**
     * Given a binary tree, return the inorder traversal of its nodes' values.
     * put node with left child in stack
     */
    //Time: O(N), Space: O(1)
    public List<Integer> inorderTraversalO1(TreeNode root) {
        List<Integer> nodes = new ArrayList<Integer>();
        TreeNode current = root;
        Stack<TreeNode> stack = new Stack<TreeNode>();
        while(current != null || stack.size() > 0){
            while(current != null) {
                stack.push(current);
                current = current.left;
            }
            if (!stack.isEmpty()) {
                current = stack.pop();
                nodes.add(current.val);
                current = current.right;
            }
        }
        return nodes;
    }

    /**
     *  Given a binary tree, return the postorder traversal of its nodes' values.
     */
    //Time: O(N), Space O(N)
    public List<Integer> postorderTraversalON(TreeNode root) {
        List<Integer> nodes = new ArrayList<Integer>();
        Stack<TreeNode> stack = new Stack<TreeNode>();
        Set<TreeNode> childrenAdded = new HashSet<TreeNode>();
        if(root != null) stack.push(root);
        while(stack.size() > 0){
            TreeNode node = stack.peek();
            if(!childrenAdded.contains(node)){
                if(node.right != null) stack.add(node.right);
                if(node.left != null) stack.add(node.left);
                childrenAdded.add(node);
            } else {
                stack.pop();
                nodes.add(node.val);
            }
        }
        return nodes;
    }

    /**
     * tracking the last visited node,
     *      if prev.left == node || prev.right == node  traverse down to children, visit left or right (left == null)
     *      if node.left == prev                        traverse back from left, visit right
     *      else                                        traverse back from children, visit itself
     */
    //Time: O(N), Space O(1)
    public List<Integer> postorderTraversalO1(TreeNode root) {
        List<Integer> nodes = new ArrayList<Integer>();
        Stack<TreeNode> stack = new Stack<TreeNode>();
        TreeNode prev = null;
        if(root != null) stack.push(root);
        while(stack.size() > 0){
            TreeNode node = stack.peek();
            if(prev == null || prev.left == node || prev.right == node){
                if(node.left != null) stack.push(node.left);
                else if(node.right != null) stack.push(node.right);
            } else if(node.left == prev){
                if(node.right != null)  stack.push(node.right);
            } else {
                nodes.add(node.val);
                stack.pop();
            }
            prev = node;
        }
        return nodes;
    }

}
