package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 上午10:32
 */
public class LOJ2_AddTwoNumber {
    //1. dummy node and carry
    //2. move prev, l1, l2 one step forward
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        if(l1 == null) return l2;
        if(l2 == null) return l1;
        ListNode dummy = new ListNode(0);
        ListNode prev = dummy;
        int carry = 0;
        while(l1 != null || l2 != null){
            int v1 = l1 != null? l1.val : 0;
            int v2 = l2 != null? l2.val : 0;
            int sum = v1 + v2 + carry;
            prev.next = new ListNode(sum % 10);
            prev = prev.next;
            carry = sum / 10;
            l1 = l1 != null? l1.next : null;
            l2 = l2 != null? l2.next : null;
        }
        if(carry != 0) prev.next = new ListNode(carry);
        return dummy.next;
    }
}
