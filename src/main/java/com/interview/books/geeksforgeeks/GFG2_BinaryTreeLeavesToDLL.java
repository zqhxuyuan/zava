package com.interview.books.geeksforgeeks;

import com.interview.leetcode.utils.TreeNode;
import com.interview.leetcode.utils.TreeNodePrinter;

/**
 * Created_By: stefanie
 * Date: 15-1-8
 * Time: 下午8:01
 */
public class GFG2_BinaryTreeLeavesToDLL {
    TreeNode prev;

    public TreeNode convert(TreeNode root){
        TreeNode dummy = new TreeNode(0);
        prev = dummy;
        visit(root, null);
        return dummy.right;
    }

    public void visit(TreeNode node, TreeNode parent){
        if(node == null) return;
        if(node.left == null && node.right == null){
            prev.right = node;
            node.left = prev;
            if(parent.left == node) parent.left = null;
            else parent.right = null;
            prev = node;
        } else {
            visit(node.left, node);
            visit(node.right, node);
        }
    }

    public static void main(String[] args){
        GFG2_BinaryTreeLeavesToDLL converter = new GFG2_BinaryTreeLeavesToDLL();
        TreeNode root = TreeNode.sampleBST();
        TreeNodePrinter.print(root);
        TreeNode head = converter.convert(root);
        TreeNodePrinter.print(root);
        while(head != null) {
            System.out.print(head.val + " ");
            head = head.right;
        }
    }
}
