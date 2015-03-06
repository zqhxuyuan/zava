package com.interview.leetcode.tree;

import com.interview.basics.model.tree.BinaryTree;
import com.interview.basics.model.tree.BinaryTreeNode;
import com.interview.leetcode.utils.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 14-11-17
 * Time: 下午3:03
 */
public class TreeOperations {

    public static int maxDepth(TreeNode root) {
        if(root == null) return 0;
        else return Math.max(maxDepth(root.left), maxDepth(root.right)) + 1;
    }

    /**
     * Given a binary tree, find its minimum depth.
     */
    //Time: O(N), Space: O(1), StackSpace: O(1)
    public int minDepthDepth(TreeNode root){
        if (root == null) return 0;
        if (root.left == null) return minDepthDepth(root.right) + 1;
        if (root.right == null) return minDepthDepth(root.left) + 1;
        return Math.min(minDepthDepth(root.left), minDepthDepth(root.right)) + 1;
    }
    //Time: O(N), Space: O(N)
    public int minDepthLayer(TreeNode root) {
        if(root == null) return 0;
        Queue<TreeNode> queue = new LinkedList<>();
        int depth = 1;
        queue.add(root);
        TreeNode rightMost = root;
        while(!queue.isEmpty()){
            TreeNode node = queue.poll();
            if(node.left == null && node.right == null) break;
            if(node.left != null) queue.add(node.left);
            if(node.right != null) queue.add(node.right);
            if(node == rightMost){
                rightMost = (node.right != null? node.right : node.left);
                depth++;
            }
        }
        return depth;
    }

    /**
     * Design an algorithm and write code to find the first common ancestor of two nodes
     */
    //Time: O(N), Space O(1)
    public TreeNode commonAncestor(TreeNode root, TreeNode n1, TreeNode n2){
        if(root == null) return null;
        if(root == n1 || root == n2) return root;
        TreeNode left = commonAncestor(root.left, n1, n2);
        TreeNode right = commonAncestor(root.right, n1, n2);
        if(left == null) return right;
        else if(right == null) return left;
        else return root;
    }

    /**
     * Write code to create a mirroring of a binary tree
     */
    public TreeNode mirrorClone(TreeNode root){
        if(root == null) return null;
        TreeNode clone = new TreeNode(root.val);
        clone.left = mirrorClone(root.right);
        clone.right = mirrorClone(root.left);
        return clone;
    }

    /**
     * Given a binary tree, determine if it is height-balanced.
     */
    //Time: O(N), Space: O(N)
    public boolean isBalanced(TreeNode root) {
        return maxDepthIfBalanced(root) != -1;
    }

    private int maxDepthIfBalanced(TreeNode root){
        if(root == null) return 0;
        int left = maxDepthIfBalanced(root.left);
        if(left == -1) return -1;
        int right = maxDepthIfBalanced(root.right);
        if(right == -1) return -1;
        return (Math.abs(left - right) > 1)? -1 : Math.max(left, right) + 1;
    }

    /** like reverse linked list, keep cur, parent, left, and do the reverse, be carefully of parent.right.  */
    //Time: O(N) Space O(1)
    public TreeNode upsideDownL(TreeNode root) {
        TreeNode p = root, parent = null, parentRight = null;
        while (p != null) {
            TreeNode left = p.left;
            p.left = parentRight;
            parentRight = p.right;
            p.right = parent;
            parent = p;
            p = left; }
        return parent;
    }

    //Time: O(N) Space O(N)
    public static TreeNode upsideDown(TreeNode root) {
        TreeNode fakeRoot = new TreeNode(0);
        fakeRoot.left = root;
        root = upsideDown(root, fakeRoot);
        root.right = null;
        root.left = null;
        return fakeRoot.right;
    }

    private static TreeNode upsideDown(TreeNode node, TreeNode prev){
        if(node == null) return prev;
        if(node.left == null){
            prev.right = node;
            return node;
        }
        prev = upsideDown(node.left, prev);
        prev.right = node;
        prev.left = node.right;
        return node;
    }

    static class NextPopulation{

        class TreeLinkNode{
            int val;
            TreeLinkNode left, right, next;

        }
        //Time: O(N), Space: O(1)
        public void connectCompleteTree(TreeLinkNode root) {
            if(root == null || root.left == null) return;
            root.left.next = root.right;
            connectCompleteTree(root.left);
            connectCompleteTree(root.right);
            TreeLinkNode right = root.right;
            while(right != null && root.next != null){   //root.next == null when root is the root node
                right.next = root.next.left;
                root = right;
                right = right.right;
            }
        }

        //Time: O(N), Space: O(N)
        public void connectNonCompleteTree(TreeLinkNode root) {
            if(root == null) return;
            List<TreeLinkNode> current = new ArrayList<>();
            current.add(root);
            while(current.size() > 0){
                List<TreeLinkNode> children = new ArrayList<>();
                for(TreeLinkNode parent : current){
                    if(parent.left != null) add(children, parent.left);
                    if(parent.right != null) add(children, parent.right);
                }
                current = children;
            }
        }

        private void add(List<TreeLinkNode> list, TreeLinkNode node){
            if(list.size() != 0) list.get(list.size() - 1).next = node;
            list.add(node);
        }
    }

    /**
     * Given a binary tree which node is a int (positive and negitive), write code to find a sub-tree which node sum is maximal.
     * based on post-order traverse
     */
    static class MaxSubTree {
        int max;
        BinaryTreeNode<Integer> maxNode;

        public BinaryTreeNode<Integer> find(BinaryTree<Integer> tree) {
            max = Integer.MIN_VALUE;
            sum(tree.getRoot());
            return maxNode;
        }

        public int sum(BinaryTreeNode<Integer> node) {
            if (node == null) return 0;
            int count = node.value;
            count += sum(node.left);
            count += sum(node.right);
            if (count > max) {
                max = count;
                maxNode = node;
            }
            return count;
        }
    }

    /**
     * Find the max distance of two node in a binary tree.
     */
    static class MaxDistance{
        int max;
        public int maxDistance(TreeNode root){
            max = 0;
            maxHeight(root);
            return max;
        }

        public int maxHeight(TreeNode root){
            if(root == null) return 0;
            int left = maxHeight(root.left);
            int right = maxHeight(root.right);
            int cur = left + right + 1;
            if(cur > max) max = cur;
            return Math.max(left, right) + 1;
        }
    }
}
