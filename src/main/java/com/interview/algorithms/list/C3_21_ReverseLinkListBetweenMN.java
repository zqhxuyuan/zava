package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.Node;

/**
 * Created_By: stefanie
 * Date: 14-11-9
 * Time: 上午11:08
 */
public class C3_21_ReverseLinkListBetweenMN {
    public static Node reverseBetween(Node head, int m, int n) {
        Node newHead = new Node(0);
        newHead.next = head;
        reverseBetween(head, m, n, 1, newHead);
        return newHead.next;
    }

    private static Node reverseBetween(Node node, int m, int n, int count, Node prev){
        if(count < m){
            reverseBetween(node.next, m, n, count + 1, node);
        } else if(count >= m && count < n){
            prev = reverseBetween(node.next, m, n, count + 1, prev);
            prev.next = node;
        } else if(count == n){
            prev.next.next = node.next;
            prev.next = node;
        }
        return node;
    }

}
