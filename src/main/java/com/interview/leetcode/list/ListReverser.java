package com.interview.leetcode.list;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-10
 * Time: 下午5:31
 */
public class ListReverser {
    public static ListNode reverse(ListNode node){
        ListNode newHead = null;
        while(node != null){
            ListNode next = node.next;
            node.next = newHead;
            newHead = node;
            node = next;
        }
        return newHead;
    }

    public static void reverse(ListNode node, ListNode tail, ListNode prev){
        ListNode newHead = tail.next;
        while(newHead != tail){
            ListNode next = node.next;
            node.next = newHead;
            newHead = node;
            node = next;
        }
        prev.next = newHead;
    }

    public ListNode reverseKGroup(ListNode head, int k) {  //0->1->2->3->4->5  -> 0->2->1->4->3->5
        if(head == null || k == 1) return head;
        ListNode dummyNode = new ListNode(0);
        dummyNode.next = head;
        ListNode prev = dummyNode;
        while(head != null){
            int i = k;
            while(head != null && i > 1){
                head = head.next;
                i--;
            }
            if(head == null) break;
            ListNode next = prev.next;
            reverse(prev.next, head, prev);  //prev: 0, head: 2, prev.next = 2, next = 1
            prev = next;
            head = prev.next;
        }
        return dummyNode.next;
    }



    public static ListNode reverseBetweenL(ListNode head, int m, int n){
        ListNode dummyNode = new ListNode(0);
        dummyNode.next = head;
        ListNode prev = dummyNode;
        n = n - m + 1;
        while(m > 1){
            head = head.next;
            prev = prev.next;
            m--;
        }
        ListNode tail = head;
        while(n > 1)  {
            tail = tail.next;
            n--;
        }

        ListNode newNode = tail.next;
        while(newNode != tail){
            ListNode next = head.next;
            head.next = newNode;
            newNode = head;
            head = next;
        }
        prev.next = newNode;
        return dummyNode.next;
    }


    public static ListNode reverseBetween(ListNode head, int m, int n) {
        ListNode fakeHead = new ListNode(0);
        fakeHead.next = head;
        reverseBetween(head, m, n, 1, fakeHead);
        return fakeHead.next;

    }

    private static ListNode reverseBetween(ListNode node, int m, int n, int count, ListNode prev){
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
