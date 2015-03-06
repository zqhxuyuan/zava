package com.interview.books.leetcode;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-10
 * Time: 下午9:37
 */
public class LCS1_SortedCycleListInsertion {
    public ListNode insert(ListNode head, int value){
        if(head == null) return null;
        ListNode node = head;
        if(value < node.val){
            while(node.next != head) node = node.next;
        } else {
            while(node.next != head && node.next.val < value) node = node.next;
        }
        ListNode newNode = new ListNode(value);
        newNode.next = node.next;
        node.next = newNode;
        return head;
    }

    public static void main(String[] args){
        ListNode head = ListNode.createList(new int[]{1,2,3,4,5,6,7,8});
        ListNode tail = head;
        while(tail.next != null) tail = tail.next;
        tail.next = head;

        LCS1_SortedCycleListInsertion inserter = new LCS1_SortedCycleListInsertion();
        inserter.insert(head, 5);
        print(head);

        inserter.insert(head, 10);
        print(head);

        inserter.insert(head, 0);
        print(head);

        head = ListNode.createList(new int[]{3});
        head.next = head;
        inserter.insert(head, 5);
        print(head);
    }

    public static void print(ListNode head){
        ListNode node = head;
        System.out.print(node.val + ", ");
        node = node.next;
        while(node != head){
            System.out.print(node.val + ", ");
            node = node.next;
        }
        System.out.println();
    }
}
