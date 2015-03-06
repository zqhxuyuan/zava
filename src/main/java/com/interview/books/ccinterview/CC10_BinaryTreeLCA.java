package com.interview.books.ccinterview;

import com.interview.basics.model.collection.hash.HashMap;
import com.interview.leetcode.utils.TreeNode;
import com.interview.leetcode.utils.TreeNodePrinter;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午12:48
 */
public class CC10_BinaryTreeLCA {
    public TreeNode LCA(TreeNode root, TreeNode n1, TreeNode n2){
        if(root == null) return null;
        if(root == n1 || root == n2) return root;
        TreeNode left = LCA(root.left, n1, n2);
        TreeNode right = LCA(root.right, n1, n2);
        if(left == null) return right;
        if(right == null) return left;
        return root;
    }

    public static void main(String[] args){
        HashMap<Integer, TreeNode> nodes = new HashMap<>();
        for (int i = 1; i < 10; i++) {
            nodes.put(i, new TreeNode(i));
        }
        nodes.get(5).left = nodes.get(2);
        nodes.get(5).right = nodes.get(7);
        nodes.get(2).left = nodes.get(1);
        nodes.get(2).right = nodes.get(3);
        nodes.get(3).right = nodes.get(4);
        nodes.get(7).left = nodes.get(6);
        nodes.get(7).right = nodes.get(9);

        TreeNode root = nodes.get(5);
        CC10_BinaryTreeLCA finder = new CC10_BinaryTreeLCA();
        TreeNodePrinter.print(root);

        System.out.println(finder.LCA(root, nodes.get(2), nodes.get(4)).val);
        System.out.println(finder.LCA(root, nodes.get(9), nodes.get(1)).val);
        System.out.println(finder.LCA(root, nodes.get(4), nodes.get(1)).val);

    }
}
