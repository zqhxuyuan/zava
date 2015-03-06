package com.interview.books.leetcode;

import com.interview.leetcode.utils.TreeNode;
import com.interview.leetcode.utils.TreeNodePrinter;

/**
 * Created_By: stefanie
 * Date: 14-12-4
 * Time: 下午9:38
 */
public class LCS11_LargestBSTSubtree {

    int maxNodes;
    TreeNode maxRoot;
    int max, min;   //tracking max and min during the post order traverse

    public TreeNode largestBST(TreeNode root){
        maxNodes = 0;
        maxRoot = null;
        max = Integer.MIN_VALUE;
        min = Integer.MAX_VALUE;
        largest(root);
        return maxRoot;
    }

    private int largest(TreeNode node) {
        if(node == null) return 0;
        boolean isBST = true;

        //visit left part
        int left = largest(node.left);
        int curMin = left == 0? node.val : min;
        if (left == -1 || (left != 0 && node.val <= max))   isBST = false;

        //visit right part
        int right = largest(node.right);
        int curMax = right == 0? node.val : max;
        if(right == -1 || (right != 0 && node.val >= min))  isBST = false;

        if (isBST) {
            min = curMin;
            max = curMax;
            int totalNodes = left + right + 1;
            if (totalNodes > maxNodes) {
                maxNodes = totalNodes;
                maxRoot = node;
            }
            return totalNodes;
        } else {
            return -1;   // This subtree is not a BST
        }
    }



    public static void main(String[] args) {
        LCS11_LargestBSTSubtree finder = new LCS11_LargestBSTSubtree();

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

        TreeNodePrinter.print(finder.largestBST(root));
        System.out.println(finder.maxNodes);

    }
}
