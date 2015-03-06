package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.LinkedList;
import com.interview.basics.model.collection.list.Node;

/**
 * Created_By: stefanie
 * Date: 14-7-10
 * Time: 下午9:30
 */
public class C3_10_ListPartition {

    public static void partition(LinkedList<Integer> list, int K){
        list.setHead(partition(list.getHead(), K));
    }

    public static Node<Integer> partition(Node<Integer> head, int K){
        Node<Integer> newHead = new Node<>(0);
        newHead.next = head;

        Node<Integer> p = newHead;
        Node<Integer> q = newHead;
        while(q.next != null){
            if(q.next.item.compareTo(K) >= 0) q = q.next; //larger or equals, move q
            else {
                if(p == q) q = q.next; //equals, not need to change, both move to next
                else {  //insert q.next after p, p move to next
                    Node<Integer> temp = q.next;
                    q.next = temp.next;
                    temp.next = p.next;
                    p.next = temp;
                }
                p = p.next;
            }
        }
        return newHead.next;
    }

    private static void swap(Node<Integer> n1, Node<Integer> n2){
        int temp = n1.item;
        n1.item = n2.item;
        n2.item = temp;
    }

    public static void partitionTwoLink(LinkedList<Integer> list, int key){
        Node<Integer> p = list.getHead();
        Node<Integer> small = null;
        Node<Integer> large = null;
        Node<Integer> largeHead = null;
        while(p != null){
            if(p.item < key) {
                if(small == null) small = p;
                else {
                    small.next = p;
                    small = p;
                }
            } else {
                if(large == null) largeHead = large = p;
                else {
                    large.next = p;
                    large = p;
                }
            }
            p = p.next;
        }
        small.next = largeHead;
    }

    public static void partitionRecursive(LinkedList<Integer> list, int key){
        Node<Integer> node = list.getHead();
        Node<Integer> small = null;
        Node<Integer> large = null;

        while(node != null){
            Node<Integer> next = node.next;
            if(node.item < key){
                node.next = small;
                small = node;
            } else {
                node.next = large;
                large = node;
            }
            node = next;
        }
        if(small == null) {
            list.setHead(large);
            return;
        } else {
            list.setHead(small);
            while(small.next != null)   small = small.next;
            small.next = large;
        }

    }

}
