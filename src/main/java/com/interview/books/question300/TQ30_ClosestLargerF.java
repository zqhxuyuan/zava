package com.interview.books.question300;


import com.interview.leetcode.utils.TreeNode;
import com.interview.leetcode.utils.TreeNodePrinter;

/**
 * Created_By: stefanie
 * Date: 14-8-21
 * Time: 上午10:43
 */
public class TQ30_ClosestLargerF {
    public static int find(TreeNode root){
        int f = getF(root);
        TreeNode node = find(root, f);
        return node.val;
    }

    public static TreeNode find(TreeNode node, int key){
        if(node.val <= key && node.right != null) return find(node.right, key);
        else if(node.val > key){
            if(node.left != null) {
                TreeNode n = find(node.left, key);
                return n == null? node: n;
            } else {
                return node;
            }
        }
        return null;
    }

    public static int getF(TreeNode root){
        TreeNode node = root;
        while(node.left != null) node = node.left;
        int smallest = node.val;

        node = root;
        while(node.right != null) node = node.right;
        int largest = node.val;

        return (smallest + largest) / 2;
    }

    public static void verify(TreeNode root){
        int f = TQ30_ClosestLargerF.getF(root);
        System.out.println(f);

        int k = TQ30_ClosestLargerF.find(root);
        System.out.println(k);
    }

    public static void main(String[] args){
        int[] data = new int[]{15, 6, 18, 3, 7, 17, 20, 2, 4, 13, 9};
        TreeNode root = TreeNode.buildBST(data);
        TreeNodePrinter.print(root);
        verify(root);

        data = new int[]{15, 6, 18, 3, 7, 14, 20, 2, 4, 11, 9};
        root = TreeNode.buildBST(data);
        TreeNodePrinter.print(root);
        verify(root);

    }
}
