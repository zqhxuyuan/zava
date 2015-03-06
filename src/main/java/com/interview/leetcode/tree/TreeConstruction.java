package com.interview.leetcode.tree;

import com.interview.leetcode.utils.ListNode;
import com.interview.leetcode.utils.TreeNode;

/**
 * Created_By: stefanie
 * Date: 14-11-14
 * Time: 上午8:27
 */
public class TreeConstruction {

    /**
     * given a sorted array, create a balanced BST
     */
    static class SortedArrayBuilder {
        public TreeNode sortedArrayToBST(int[] num) {
            return createTree(num, 0, num.length - 1);
        }
        public TreeNode createTree(int[] num, int low, int high) {
            if (low > high) return null;
            int mid = low + (high - low) / 2;
            TreeNode node = new TreeNode(num[mid]);
            node.left = createTree(num, low, mid - 1);
            node.right = createTree(num, mid + 1, high);
            return node;
        }
    }

    /**
     * given a sorted list, create a balanced BST
     */
    static class SortedListBuilder{
        ListNode current;
        public TreeNode sortedListToBST(ListNode head) {
            if(head == null) return null;
            current = head;
            int length = length(head);
            return buildTree(length);
        }

        public int length(ListNode head){
            int length = 0;
            while(head != null){
                head = head.next;
                length++;
            }
            return length;
        }

        public TreeNode buildTree(int length){
            if(length == 0) return null;
            TreeNode left = buildTree(length / 2);
            TreeNode node = new TreeNode(current.val);
            current = current.next;
            node.left = left;
            node.right = buildTree(length - 1 - length/2);
            return node;
        }
    }

    static class PreInOrderBuilder{
        int offset = 0;
        public TreeNode buildTree(int[] preorder, int[] inorder) {
            offset = 0;
            return buildTree(preorder, inorder, 0, inorder.length - 1);
        }

        public TreeNode buildTree(int[] preorder, int[] inorder, int low, int high){
            if(low > high) return null;
            int cur = preorder[offset++];
            int mid = low;
            while(mid <= high && inorder[mid] != cur) mid++;
            TreeNode node = new TreeNode(cur);
            node.left = buildTree(preorder, inorder, low, mid - 1);
            node.right = buildTree(preorder, inorder, mid + 1, high);
            return node;
        }
    }

    static class PostInOrderBuilder{
        int offset;
        public TreeNode buildTree(int[] inorder, int[] postorder) {
            offset = inorder.length - 1;
            return buildTree(postorder, inorder, 0, inorder.length - 1);
        }

        public TreeNode buildTree(int[] postorder, int[] inorder, int low, int high){
            if(low > high) return null;
            int cur = postorder[offset--];
            int mid = high;
            while(mid >= low && inorder[mid] != cur) mid--;
            TreeNode node = new TreeNode(cur);
            node.right = buildTree(postorder, inorder, mid + 1, high);
            node.left = buildTree(postorder, inorder, low, mid - 1);
            return node;
        }
    }

    /**
     * Given a binary tree, flatten it to a linked list in-place.
     * It's a pre-order traverse
     */
    static class TreeFattern{
        TreeNode last = null;
        public void flatten(TreeNode root) {
            if(root == null) return;
            TreeNode right = root.right;
            TreeNode left = root.left;
            if(last != null){
                last.left = null;
                last.right = root;
            }
            last = root;
            flatten(left);
            flatten(right);
        }
    }

    /**
     * Write a method to transfer a BinarySearchTree to a sorted LinkedList without using extra space.
     * Based on in-order traverse
     */
    static class BSTFattern{

        public static TreeNode fattern(TreeNode root){
            if(root == null) return null;
            return fattern(root, null);
        }

        private static TreeNode fattern(TreeNode node, TreeNode pre) {
            if(node.left != null) pre = fattern(node.left, pre);
            node.left = pre;
            if(pre != null) pre.right = node;
            if(node.right != null) return fattern(node.right, node);
            else return node;
        }
    }
    /**
     * Write a method to transfer a BinarySearchTree to a sorted LinkedList without using extra space.
     * Keep tracking the min and max
     */
    static class BSTFatternMinMax{
        TreeNode min;
        TreeNode max;

        public TreeNode fattern(TreeNode root){
            if(root == null) return null;
            max = null;
            min = null;
            fatternNode(root);
            return min;
        }

        public void fatternNode(TreeNode node){
            if(node == null) return;
            fatternNode(node.left);
            if(min == null) min = node;
            if(max != null) {
                max.right = node;
                node.left = max;
            }
            max = node;
            fatternNode(node.right);
        }
    }

    /**
     * Two elements of a binary search tree (BST) are swapped by mistake.
     * Recover the tree without changing its structure.
     */
    //Time: O(N), Space: O(1)
    static class BSTRecover{
        TreeNode first = null;
        TreeNode second = null;
        TreeNode last = null;
        public void recoverTree(TreeNode root) {
            findBreakPoint(root);
            int temp = first.val;
            first.val = second.val;
            second.val = temp;
        }

        public void findBreakPoint(TreeNode node){
            if(node == null) return;
            findBreakPoint(node.left);
            if(last != null && last.val > node.val){//find a break point;
                if(first == null) first = last;
                second = node;
            }
            last = node;
            findBreakPoint(node.right);
        }
    }

}
