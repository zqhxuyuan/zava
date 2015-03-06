package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinaryTree;
import com.interview.basics.model.tree.BinaryTreeNode;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/17/14
 * Time: 9:04 AM
 */
public class C5_6_BinaryTreeMatcher {

    public static boolean equals(BinaryTree t1, BinaryTree t2){
        return equals(t1.getRoot(), t2.getRoot());
    }

    public static boolean equals(BinaryTreeNode n1, BinaryTreeNode n2){
        if(n1 == null && n2 == null) return true;
        if(n1 != null && n2 != null && n1.value.equals(n2.value) && n1.size == n2.size){
            boolean leftEquals = equals(n1.left, n2.left);
            boolean rightEquals = equals(n1.right, n2.right);
            return leftEquals && rightEquals;
        }
        return false;
    }
}
