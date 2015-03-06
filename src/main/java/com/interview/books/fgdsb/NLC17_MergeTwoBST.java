package com.interview.books.fgdsb;

import com.interview.leetcode.utils.TreeNode;
import com.interview.leetcode.utils.TreeNodePrinter;

/**
 * Created_By: stefanie
 * Date: 15-2-2
 * Time: 下午12:38
 */
public class NLC17_MergeTwoBST {
    TreeNode lastVisited;

    public TreeNode merge(TreeNode root1, TreeNode root2){
        TreeNode head1 = flatten(root1);
        TreeNode head2 = flatten(root2);
        TreeNode head = mergeList(head1, head2);
        return buildTree(head);
    }


    private TreeNode flatten(TreeNode root) {
        TreeNode dummy = new TreeNode(0);
        lastVisited = dummy;
        flattenVisit(root);
        lastVisited.right = null;
        return dummy.right;
    }

    private void flattenVisit(TreeNode node){
        if(node == null) return;
        flattenVisit(node.left);
        TreeNode right = node.right;
        lastVisited.right = node;
        node.left = null;
        lastVisited = node;
        flattenVisit(right);
    }


    private TreeNode mergeList(TreeNode head1, TreeNode head2) {
        TreeNode dummy = new TreeNode(0);
        TreeNode pre = dummy;
        while(head1 != null && head2 != null){
            if(head1.val <= head2.val){
                pre.right = head1;
                head1 = head1.right;
            } else {
                pre.right = head2;
                head2 = head2.right;
            }
            pre = pre.right;
        }
        pre.right = head1 == null? head2 : head1;
        return dummy.right;
    }

    private TreeNode buildTree(TreeNode head){
        lastVisited = head;
        int length = length(head);
        return buildTreeVisit(length);
    }

    private TreeNode buildTreeVisit(int length){
        if(length == 0) return null;
        else if(length == 1){
            TreeNode node = lastVisited;
            lastVisited = lastVisited.right;
            node.right = null;
            return node;
        } else {
            TreeNode left = buildTreeVisit(length / 2);
            TreeNode node = lastVisited;
            lastVisited = lastVisited.right;
            node.left = left;
            node.right = buildTreeVisit(length - length/2 - 1);
            return node;
        }
    }

    private int length(TreeNode head){
        int length = 0;
        while(head != null){
            length++;
            head = head.right;
        }
        return length;
    }

    public static void main(String[] args){
        NLC17_MergeTwoBST merger = new NLC17_MergeTwoBST();
        TreeNode root1 = TreeNode.buildBST(new int[]{4,2,5,1,3});
        TreeNodePrinter.print(root1);
        TreeNode root2 = TreeNode.buildBST(new int[]{7,5,9,4,8,10});
        TreeNodePrinter.print(root2);
        TreeNode root = merger.merge(root1, root2);
        TreeNodePrinter.print(root);
    }

}
