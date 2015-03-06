package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinaryTree;
import com.interview.basics.model.tree.BinaryTreeNode;

/**
 * Created_By: stefanie
 * Date: 14-8-30
 * Time: 下午9:29
 *
 * using parent as the next pointer
 */
public class C5_22_NextNodeSameLayer {

    public static void findNext(BinaryTree tree){
        BinaryTreeNode node = tree.getRoot();
        findNext(node);
    }

    private static void findNext(BinaryTreeNode node){
        if(node.left != null) {
            node.left.parent = node.right;
            findNext(node.left);
        }
        if(node.right != null) {
            if (node.parent != null) node.right.parent = node.parent.left;
            else node.right.parent = null;
            findNext(node.right);
        }

    }

    public static void findNextComplete(BinaryTree tree){
        BinaryTreeNode node = tree.getRoot();
        connect(node);
    }

    private static void connect(BinaryTreeNode node){
        if(node == null) return;
        connect(node.left);
        connect(node.right);
        connect(node.left, node.right);
    }

    private static void connect(BinaryTreeNode left, BinaryTreeNode right){
        while(left != null){
            left.parent = right;
            left = left.right;
            right = right.left;
        }
    }

    public static void findNextUncomplete(BinaryTree tree){
        connectUncomplete(tree.getRoot());
    }

    public static void connectUncomplete(BinaryTreeNode root) {
        if(root == null) return;
        if(root.left != null && root.right != null) root.left.parent = root.right;
        connectUncomplete(root.right);
        connectUncomplete(root.left);
        connectUncomplete(root.parent, root.right);
        connectUncomplete(root.parent, root.left);
    }

    private static void connectUncomplete(BinaryTreeNode root, BinaryTreeNode prev){
        if(prev == null || root == null) return;
        while(root != null && root.left == null && root.right == null) root = root.parent;
        if(root == null) return;
        BinaryTreeNode next = root.left != null? root.left : root.right;
        if(prev.parent == null)   prev.parent = next;
        connectUncomplete(next, prev.right);
        connectUncomplete(next, prev.left);
    }
}
