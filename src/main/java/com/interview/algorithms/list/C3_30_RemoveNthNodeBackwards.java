package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.Node;

/**
 * Created_By: stefanie
 * Date: 14-11-6
 * Time: 上午9:16
 */
public class C3_30_RemoveNthNodeBackwards {
    public static Node removeNthFromEnd(Node head, int n) {
        if(head == null) return null;
        Node p = head;
        Node q = head;
        while(n > 0 && p.next != null){
            p = p.next;
            n--;
        }
        if(n == 1) return head.next;
        if(n > 1) return head;
        while(p.next != null){
            p = p.next;
            q = q.next;
        }
        q.next = q.next.next;
        return head;
    }
}
