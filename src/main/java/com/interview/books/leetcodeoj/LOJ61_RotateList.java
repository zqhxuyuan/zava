package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午8:14
 */
public class LOJ61_RotateList {
    //1. get length and normalize n = n % length;
    //2. when(n > 0) n-- and fast = fast.next;
    //3. then fast and slow go together when fast.next != null;
    public ListNode rotateRight(ListNode head, int n) {
        if(head == null || head.next == null) return head;
        int length = length(head);
        n = n % length;
        if(n == 0) return head;
        ListNode fast = head;
        ListNode slow = head;
        while(fast != null && n > 0){
            fast = fast.next;
            n--;
        }
        while(fast.next != null){
            fast = fast.next;
            slow = slow.next;
        }
        fast.next = head;
        head = slow.next;
        slow.next = null;
        return head;
    }

    public int length(ListNode head){
        int length = 0;
        while(head != null){
            length++;
            head = head.next;
        }
        return length;
    }
}
