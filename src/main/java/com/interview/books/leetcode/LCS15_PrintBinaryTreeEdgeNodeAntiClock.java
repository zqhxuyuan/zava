package com.interview.books.leetcode;

import com.interview.leetcode.utils.TreeNode;
import com.interview.leetcode.utils.TreeNodePrinter;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 下午8:13
 */
public class LCS15_PrintBinaryTreeEdgeNodeAntiClock {
    public void print(TreeNode root){
        if(root == null) return;
        System.out.println(root.val + ", ");
        printLeftEdges(root.left, true);
        printRightEdges(root.right, true);
    }

    public void printLeftEdges(TreeNode node, boolean print){
        if(node == null) return;
        if(print || (node.left == null && node.right == null))
            System.out.println(node.val + ", ");
        printLeftEdges(node.left, print);
        printLeftEdges(node.right, node.left == null? print : false);
    }

    public void printRightEdges(TreeNode node, boolean print){
        if(node == null) return;
        printRightEdges(node.left, node.right == null? print : false);
        printRightEdges(node.right, print);
        if(print || (node.left == null && node.right == null))
            System.out.println(node.val + ", ");
    }

    public static void main(String[] args){
        LCS15_PrintBinaryTreeEdgeNodeAntiClock printer = new LCS15_PrintBinaryTreeEdgeNodeAntiClock();
        TreeNode node = TreeNode.sampleBST();

        TreeNodePrinter.print(node);

        printer.print(node);
    }
}
