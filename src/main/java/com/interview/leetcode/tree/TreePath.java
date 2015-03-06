package com.interview.leetcode.tree;

import com.interview.basics.model.tree.BinaryTree;
import com.interview.basics.model.tree.BinaryTreeNode;
import com.interview.leetcode.utils.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-22
 * Time: 下午8:38
 */
public class TreePath {
    /**
     * You are given a binary tree in which each node contains a value. Design an algorithm
     to print all paths which sum up to that value. Note that it can be any path in the tree
     - it does not have to start at the root.
     */
    static class PathSum {
        public static void findSum(BinaryTree<Integer> tree, int sum) {
            int height = getHeight(tree.getRoot());
            int[] buffer = new int[height];
            findSum(tree.getRoot(), sum, buffer, 0);
        }

        private static void findSum(BinaryTreeNode<Integer> node, int sum, int[] buffer, int level) {
            if (node == null) return;
            buffer[level] = node.value;
            int tmp = 0;
            for (int i = level; i >= 0; i--) {
                tmp += buffer[i];
                if (tmp == sum) printPath(buffer, i, level);
            }
            findSum(node.left, sum, buffer, level + 1);
            findSum(node.right, sum, buffer, level + 1);
            buffer[level] = 0;
        }

        private static void printPath(int[] buffer, int start, int end) {
            for (int i = start; i <= end; i++) System.out.print(buffer[i] + " ");
            System.out.println();
        }

        private static int getHeight(BinaryTreeNode<Integer> node) {
            if (node == null) return 0;
            int left = getHeight(node.left);
            int right = getHeight(node.right);
            return Math.max(left, right) + 1;
        }
    }

    /** Given a binary tree, find the maximum path sum. The path may start and end at any node in the tree.
     * For example: Given the below binary tree,
     *      1
     *     / \
     *    2   3
     *  Return 6.
     *
     * Solutions:
     * The max path should have following 4 cases:
     *      i. max(left subtree) + node
     *      ii. max(right subtree) + node
     *      iii. max(left subtree) + max(right subtree) + node
     *      iv. the node itself  (!!!!important)
     * The path of root is depends on both left and right subtree, should use post-order traverse.
     * It's a standard divide and conquer sample for Tree, in post-order traverse.
     *
     * Tricks:
     *  1. Define a simplest and clear calculation method for the variable you interested (max path), may have several different cases.
     *  2. For tree problem, find a suitable traverse: pre-order, in-order and post-order.
     *  3. Using divide and conquer every TreeNode, do the same procedure on its children.
     *  4. Consider the cases if result go negative, do we need change it to 0.    for the singlePath
    */
    static class MaximumPathSum {
        int maxSum;

        public int maxPathSum(TreeNode root) {
            maxSum = Integer.MIN_VALUE;
            visitTree(root);
            return maxSum;
        }

        public int visitTree(TreeNode node){
            if(node == null) return 0;
            //Divide
            int left = visitTree(node.left);
            int right = visitTree(node.right);
            //Conquer
            maxSum = Math.max(maxSum, left + right + node.val);
            int singleMax = Math.max(left, right) + node.val;
            return singleMax > 0? singleMax : 0;
        }
    }

    static class PathSumToLeaf{

        public boolean hasPathSum(TreeNode root, int sum) {
            if(root == null) return false;
            sum -= root.val;
            if(root.left == null && root.right == null && sum == 0) return true;
            return hasPathSum(root.left, sum) || hasPathSum(root.right, sum);
        }

        public List<List<Integer>> pathSum(TreeNode root, int sum) {
            List<List<Integer>> paths = new ArrayList<>();
            List<Integer> path = new ArrayList<>();
            pathSum(root, sum, path, paths);
            return paths;
        }

        public void pathSum(TreeNode node, int sum, List<Integer> path, List<List<Integer>> paths){
            if(node == null) return;
            sum -= node.val;
            path.add(node.val);
            if(node.left == null && node.right == null && sum == 0){ //found a path
                List<Integer> answer = new ArrayList<Integer>();
                answer.addAll(path);
                paths.add(answer);
            } else {
                pathSum(node.left, sum, path, paths);
                pathSum(node.right, sum, path, paths);
            }
            path.remove(path.size() - 1);
        }
    }
}
