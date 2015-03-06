package com.interview.leetcode.math;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-11-13
 * Time: 下午9:50
 *
 * You are given two linked lists representing two non-negative numbers. The digits are stored in reverse order and each of their nodes contain a single digit. Add the two numbers and return it as a linked list.
 *      Input: (2 -> 4 -> 3) + (5 -> 6 -> 4)
 *      Output: 7 -> 0 -> 8
 *
 * Tricks:
 *  1. fake head
 *  2. create a general function to calculate every offset number
 */
public class AddTwoNumber {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        if(l1 == null) return l2;
        if(l2 == null) return l1;
        ListNode head = new ListNode(0);
        ListNode p = head;
        int carry = 0;
        while(l1 != null || l2 != null){
            int i = l1 == null? 0 : l1.val;
            int j = l2 == null? 0 : l2.val;
            carry = add(i, j, p, carry);
            p = p.next;
            l1 = l1 == null? l1 : l1.next;
            l2 = l2 == null? l2 : l2.next;
        }
        if(carry != 0){
            p.next = new ListNode(carry);
        }
        return head.next;
    }

    private int add(int v1, int v2, ListNode prev, int carry){
        int sum = v1 + v2 + carry;
        ListNode node = new ListNode(sum % 10);
        prev.next = node;
        return sum / 10;
    }
}
