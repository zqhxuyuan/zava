package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinarySearchTree;
import com.interview.basics.model.tree.BinaryTreeNode;

/**
 * Created_By: stefanie
 * Date: 14-8-21
 * Time: 上午10:43
 */
public class C5_21_ClosestLargerF {
    public static int find(BinarySearchTree<Integer> tree){
        int f = getF(tree);
        BinaryTreeNode<Integer> node = find(tree, tree.getRoot(), f);
        return node.value;
    }

    public static BinaryTreeNode find(BinarySearchTree<Integer> tree, BinaryTreeNode<Integer> node, int key){
        if(node.value == key) return tree.successor(node);
        else if(node.value < key && node.right != null) return find(tree, node.right, key);
        else if(node.value > key){
            if(node.left != null) {
                BinaryTreeNode n = find(tree, node.left, key);
                return n == null? node: n;
            } else {
                return node;
            }
        }
        return null;
    }

    public static int getF(BinarySearchTree<Integer> tree){
        BinaryTreeNode<Integer> node = tree.getRoot();
        while(node.left != null) node = node.left;
        int smallest = node.value;

        node = tree.getRoot();
        while(node.right != null) node = node.right;
        int largest = node.value;

        return (smallest + largest) / 2;
    }
}
