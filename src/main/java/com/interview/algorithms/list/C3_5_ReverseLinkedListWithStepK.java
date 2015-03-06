package com.interview.algorithms.list;


import com.interview.basics.model.collection.list.Node;

/**
 * Created_By: stefanie
 * Date: 2/22/14
 * Time: 6:52 PM
 *
 * Write a routine to reverse every k nodes in a given linked list without using additional memory.
 *
 * input : 1,2,3,4,5,6 k:3
   output: 3,2,1,6,5,4
 
 */
public class C3_5_ReverseLinkedListWithStepK {

    public static Node reverse(Node head, int k) {
        if(k <= 1) return head;
        Node newHead = new Node(0);
        newHead.next = head;
        Node p = newHead;
        Node q = newHead;
        int counter = 0;
        while(q.next != null){   //find the begin and end, then reverse the node between them
             counter = 0;
             while(q.next != null && counter < k - 1) {
                 q = q.next;
                 counter++;
             }
             if(q.next == null) break;
             //reverse element between p.next ~ q.next;  tracking prev and next node
             Node next = q.next.next;
             Node prev = p.next;
             p.next = q.next;
             reverse(prev, q.next);
             prev.next = next;
             p = prev;
             q = prev;
        }
        return newHead.next;
    }

    private static Node reverse(Node current, Node tail){
        if(current.next == tail)  tail.next = current;
        else reverse(current.next, tail).next = current;
        return current;
    }
    
    public static Node reverseUsingBetween(Node head, int k){
        if(k <= 1) return head;
        Node newHead = new Node(0);
        newHead.next = head;
        Node prev = newHead;
        Node next = head;
        while(next != null){
            prev = reverseBetween(next, 1, k, 1, prev);
            if(prev == null) next = null;
            else next = prev.next;
        }
        return newHead.next;
    }

    private static Node reverseBetween(Node node, int m, int n, int count, Node prev){   //reverse m - n start from node
        if(count < m){
            if(node.next == null) return null;
            reverseBetween(node.next, m, n, count + 1, node);
        } else if(count >= m && count < n){
            if(node.next == null) return null;
            prev = reverseBetween(node.next, m, n, count + 1, prev);
            if(prev == null) return null;
            prev.next = node;
        } else if(count == n){
            prev.next.next = node.next;
            prev.next = node;
        }
        return node;
    }


}
