package com.interview.algorithms.tree;

import com.interview.basics.model.collection.list.LinkedList;
import com.interview.basics.model.collection.list.Node;
import com.interview.basics.model.tree.BinarySearchTree;
import com.interview.basics.model.tree.BinaryTreeNode;

/**
 * Created_By: stefanie
 * Date: 14-11-10
 * Time: 上午11:54
 */
public class C5_11A_SortedList2BalancedBST<T extends Comparable> {
    Node<T> current;
    public BinarySearchTree<T> create(LinkedList<T> list){
        int length = length(list.getHead());
        current = list.getHead();
        return new BinarySearchTree<>(createTree(length));
    }

    private BinaryTreeNode<T> createTree(int length){    //leveraging in-order traverse
        if(length <= 0) return null;
        BinaryTreeNode<T> node = new BinaryTreeNode<>(null);
        node.setLeft(createTree(length / 2));
        node.value = current.item;
        current = current.next;
        node.setRight(createTree(length - 1 - length / 2));
        return node;
    }

    private int length(Node<T> head){
        int length = 0;
        while(head != null){
            head = head.next;
            length++;
        }
        return length;
    }
}
