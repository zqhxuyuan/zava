package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.Node;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/10/14
 * Time: 10:05 AM
 */
public class C3_9_DeleteNodeInSinglyList<T> {

    public void deleteNode(Node<T> node){
        node.item = node.next.item;
        node.next = node.next.next;
    }
}
