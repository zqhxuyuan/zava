package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinarySearchTree;
import com.interview.basics.model.tree.BinaryTreeNode;

/**
 * Created_By: stefanie
 * Date: 14-10-22
 * Time: 下午8:28
 */
public class C5_17_BST2LinkedListNoExtraSpace {
    public static BinaryTreeNode transfer(BinarySearchTree tree){
        BinaryTreeNode head = tree.min();
        transfer(tree.getRoot(), null);
        return head;
    }

    private static BinaryTreeNode transfer(BinaryTreeNode node, BinaryTreeNode preNode){
        if(node.left != null) preNode = transfer(node.left, preNode);
        node.left = preNode;
        if(preNode != null) preNode.right = node;
        if(node.right != null) return transfer(node.right, node);
        else return node;
    }
}
