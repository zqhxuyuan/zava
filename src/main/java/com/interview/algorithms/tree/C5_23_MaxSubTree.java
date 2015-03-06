package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinaryTree;
import com.interview.basics.model.tree.BinaryTreeNode;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/18/14
 * Time: 4:41 PM
 */
public class C5_23_MaxSubTree {
    int max;
    BinaryTreeNode<Integer> maxNode;

    public BinaryTreeNode<Integer> find(BinaryTree<Integer> tree){
        max = Integer.MIN_VALUE;
        sum(tree.getRoot());
        return maxNode;
    }

    public int sum(BinaryTreeNode<Integer> node){
        if(node == null) return 0;
        int count = node.value;
        count += sum(node.left);
        count += sum(node.right);
        if(count > max) {
            max = count;
            maxNode = node;
        }
        return count;
    }
}
