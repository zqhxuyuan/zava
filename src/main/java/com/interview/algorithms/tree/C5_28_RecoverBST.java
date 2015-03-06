package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinaryTreeNode;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 11/11/14
 * Time: 11:52 AM
 */
public class C5_28_RecoverBST {
    BinaryTreeNode<Integer> first = null;
    BinaryTreeNode<Integer> second = null;
    BinaryTreeNode<Integer> last = new BinaryTreeNode(Integer.MIN_VALUE); ;

    private void traverse(BinaryTreeNode<Integer> root){
        if(root == null) return;
        traverse(root.left);
        if(first == null && root.value < last.value) first = last;
        if(first != null && root.value < last.value) second = root;
        last = root;
        traverse(root.right);
    }

    public void recoverTree(BinaryTreeNode<Integer> root) {
        traverse(root);
        if(first != null && second != null){
            int temp = first.value;
            first.value = second.value;
            second.value = temp;
        }
    }
}
