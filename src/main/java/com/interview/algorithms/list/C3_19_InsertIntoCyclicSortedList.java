package com.interview.algorithms.list;

/**
 * Created_By: stefanie
 * Date: 14-10-20
 * Time: 下午9:52
 */
public class C3_19_InsertIntoCyclicSortedList {
    static class Node{
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    public static Node insert(Node head, int k){
        Node pre = head;
        Node cur = head.next;
        while(cur.value >= k && cur != head){
            cur = cur.next;
            pre = pre.next;
        }
        Node newNode = new Node(k);
        pre.next = newNode;
        newNode.next = cur;
        return k < head.value? newNode : head;
    }
}
