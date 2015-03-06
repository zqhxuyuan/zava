package com.interview.books.leetcode;

import com.interview.leetcode.utils.TreeNode;
import com.interview.leetcode.utils.TreeNodePrinter;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 下午4:50
 */
public class LCS12_LargestBST {
    int maxNodes;
    TreeNode child;
    TreeNode largestBST;
    public TreeNode largestBST(TreeNode root){
        maxNodes = 0;
        child = null;
        largest(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return largestBST;
    }

    private int largest(TreeNode node, int min, int max){
        if(node == null) return 0;
        if(node.val < min || node.val > max){
            largest(node, Integer.MIN_VALUE, Integer.MAX_VALUE);
            return 0;
        } else {
            TreeNode parent = new TreeNode(node.val);
            int left = largest(node.left, min, node.val);
            parent.left = left == 0? null: child;
            int right = largest(node.right, node.val, max);
            parent.right = right == 0? null: child;

            int totalNodes = left + right + 1;
            if (totalNodes > maxNodes) {
                maxNodes = totalNodes;
                largestBST = parent;
            }
            child = parent;
            return totalNodes;
        }
    }

    public static void main(String[] args){
        TreeNode[] nodes = new TreeNode[21];
        for(int i = 0; i < nodes.length; i++) nodes[i] = new TreeNode(i);
        /**
         *          15
         *       10    20
         *     5    7
         *        2   5
         *      0  8 3
         */
        nodes[15].left = nodes[10];
        nodes[15].right = nodes[20];
        nodes[10].left = new TreeNode(5);
        nodes[10].right = nodes[7];
        nodes[7].left = nodes[2];
        nodes[7].right = nodes[5];
        nodes[2].left = nodes[0];
        nodes[2].right = nodes[8];
        nodes[5].left = nodes[3];

        TreeNode root = nodes[15];
        LCS12_LargestBST finder = new LCS12_LargestBST();
        TreeNode node = finder.largestBST(root);  //4
        TreeNodePrinter.print(node);
        System.out.println(finder.maxNodes);
    }
}
