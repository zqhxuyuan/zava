package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.LinkedList;
import com.interview.basics.model.collection.list.Node;

/**
 * Created_By: stefanie
 * Date: 14-7-19
 * Time: 下午5:52
 */
public class C3_15_ListReverse {

    public static void reverseByLoop(LinkedList list){
        Node current = null;
        Node next = list.getHead();
        while(next != null){
            Node nextnext = next.next;
            next.next = current;
            current = next;
            next = nextnext;
        }
        list.setHead(current);

    }

    public static void reverseRecursive(LinkedList list){
        Node head = list.getHead();
        while(head.next != null) head = head.next;
        reverseRecursive(list.getHead()).next = null;
        list.setHead(head);
    }

    public static Node reverseRecursive(Node node){
        if(node.next == null) return node;
        reverseRecursive(node.next).next = node;
        return node;
    }


}
