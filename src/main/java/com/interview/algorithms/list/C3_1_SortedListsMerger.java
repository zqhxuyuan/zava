package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.LinkedList;
import com.interview.basics.model.collection.list.Node;
import sun.awt.image.ImageWatched;

public class C3_1_SortedListsMerger<T extends Comparable> {

    /**
     * Repeatedly do the followings until on
     * e list reaches the end.
     *     1) Pick the smaller node from the current nodes of the two lists.
     *     2) Append the smaller one to the result list
     *     3) For the current node of each list, shift it down the list until
     *     the node's value is different and bigger than the tail of result list.
     *
     * For the remaining list with elements not visited, repeatedly do the followings:
     *     1) Shift the current node of the remaining list until the node's value
     *     is different and bigger than the tail of result list.
     *     2) Append the node to the result list
     *
     */
    public LinkedList<T> mergeRemoveDuplicate(LinkedList<T> list1, LinkedList<T> list2){
        Node<T> p1 = list1.getHead();
        Node<T> p2 = list2.getHead();

        Node<T> head = null;
        Node<T> tail = null;

        while (p1 != null && p2 != null) {   // while #1
            // determine smaller node
            // Always append smallest node to the result list
            Node smallerNode = p1.item.compareTo(p2.item) <= 0 ? p1 : p2;
            // shift node1 and node2 pointer to a node with bigger different value
            while (p1 != null && p1.item.compareTo(smallerNode.item) == 0)
                p1 = p1.next;
            while (p2 != null && p2.item.compareTo(smallerNode.item) == 0)
                p2 = p2.next;
            // append smallerNode to resultList
            smallerNode.next = null ;
            if (head == null) {
                head = smallerNode ;
                tail = head;
            } else {
                tail.next = smallerNode;
                tail = tail.next;
            }
        }
        Node<T> remainingNode = p1 == null ? p2 : p1;
        if(head == null)  head = tail = remainingNode;
        while(remainingNode != null) {
            if(remainingNode.item.compareTo(tail.item) != 0) {
                tail.next = remainingNode;
                tail = tail.next;
            }
            remainingNode = remainingNode.next;
            tail.next = null;
        }

        return new LinkedList<>(head);
    }

    public LinkedList<T> merge(LinkedList<T> list1, LinkedList<T> list2){
        Node<T> p1 = list1.getHead();
        Node<T> p2 = list2.getHead();

        Node<T> head = null;
        Node<T> tail = null;

        while(p1 != null && p2 != null){
            while(p1 != null && p1.item.compareTo(p2.item) <= 0){
                if(head == null) head = p1;
                tail = p1;
                p1 = p1.next;
            }
            if(tail != null) tail.next = p2;
            if(head == null) head = p2;
            tail = p2;
            p2 = p1;
            p1 = tail.next;
        }

        Node<T> remainingNode = p1 == null ? p2 : p1;
        if(head == null)  head = remainingNode;
        else tail.next = remainingNode;

        return new LinkedList<>(head);
    }

}
