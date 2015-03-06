package com.interview.algorithms.tree;

import com.interview.basics.model.tree.BinaryTree;
import com.interview.basics.model.tree.BinaryTreeNode;

import java.util.Iterator;

/**
 * Created_By: stefanie
 * Date: 14-7-16
 * Time: 下午10:14
 */

public class C5_5_BSTProver<T extends Comparable> {

    public boolean isBST(BinaryTree<T> tree){
        AddListProcessor p = new AddListProcessor();
        C5_1_TreeTraverse.traverseByInOrder(tree.getRoot(), p);
        Iterator<T> itr = p.list.iterator();
        T current = itr.next();
        while(itr.hasNext()){
            if(current.compareTo(itr.next()) > 0) return false;
        }
        return true;
    }

    private T lastVal = null;
    public boolean isValidBST(BinaryTree<T> tree){
        this.lastVal = null;
        return isValidBST(tree.getRoot());
    }

    public boolean isValidBST(BinaryTreeNode<T> node) {
        if (node == null) return true;
        if (!isValidBST(node.left)) return false;
        if (lastVal != null && lastVal.compareTo(node.value) >= 0) return false;
        lastVal = node.value;
        if (!isValidBST(node.right)) return false;
        return true;
    }

    public boolean isValidBSTMinMax(BinaryTreeNode<T> node){
        if(node == null) return true;
        if(!isValidBSTMinMax(node.left)) return false;
        if(!isValidBSTMinMax(node.right)) return false;
        if((node.left != null && max(node.left).compareTo(node.value) >= 0 )
                || (node.right != null && min(node.right).compareTo(node.value) <= 0)) return false;
        return true;
    }

    private T max(BinaryTreeNode<T> node){
        if(node.right == null) return node.value;
        return max(node.right);
    }

    private T min(BinaryTreeNode<T> node){
        if(node.left == null) return node.value;
        return min(node.left);
    }
}
