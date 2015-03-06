package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-28
 * Time: 下午12:09
 */
public class LOJ143_ReorderList {
    //1. find the middle element
    //2. reverse the middle element till tail
    //3. interleaving the two list: front half and back half
    public void reorderList(ListNode head) {
        if(head == null) return;
        ListNode fast = head;
        ListNode slow = head;
        while(fast.next != null && fast.next.next != null){
            fast = fast.next.next;
            slow = slow.next;
        }
        ListNode second = slow.next;
        slow.next = null;
        second = reverse(second);
        interleaving(head, second);
    }

    public ListNode reverse(ListNode head){
        ListNode prev = null;
        while(head != null){
            ListNode next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }

    public void interleaving(ListNode l1, ListNode l2){
        ListNode dummy = new ListNode(0);
        boolean odd = true;
        while(l1 != null && l2 != null){
            if(odd){
                dummy.next = l1;
                l1 = l1.next;
            } else {
                dummy.next = l2;
                l2 = l2.next;
            }
            dummy = dummy.next;
            odd = !odd;
        }
        dummy.next = l1 != null? l1 : l2;
    }
}
