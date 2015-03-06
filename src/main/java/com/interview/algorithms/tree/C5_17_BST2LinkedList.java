package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinarySearchTree;
import com.interview.basics.model.tree.BinaryTreeNode;

/**
 * Created_By: stefanie
 * Date: 14-7-18
 * Time: 下午8:45
 *
 * Write a method to transfer a BinarySearchTree to a sorted LinkedList without using extra space.
 *
 * Use traverse method, keep tracking min and max of the subtree.
 *    When visit left sub-tree, max of left sub-tree .next = node
 *    When visit right sub-tree, min of right sub-tree .previous = node
 */
class MaxMinNode{
    BinaryTreeNode max;
    BinaryTreeNode min;

    public MaxMinNode(BinaryTreeNode min, BinaryTreeNode max){
        this.min = min;
        this.max = max;
    }

    public BinaryTreeNode getMax() {
        return max;
    }

    public BinaryTreeNode getMin() {
        return min;
    }
}

public class C5_17_BST2LinkedList {

    public static MaxMinNode transfer(BinarySearchTree tree){
        MaxMinNode node = transfer(tree.getRoot());
        return node;
    }

    public static MaxMinNode transfer(BinaryTreeNode node){
        MaxMinNode cmm = new MaxMinNode(node, node);
        if(node.left != null) {
            MaxMinNode mm = transfer(node.left);
            mm.max.setRight(node);
            node.setLeft(mm.max);
            cmm.min = mm.min;
        }
        if(node.right != null) {
            MaxMinNode mm = transfer(node.right);
            mm.min.setLeft(node);
            node.setRight(mm.min);
            cmm.max = mm.max;
        }
        return cmm;
    }

}
