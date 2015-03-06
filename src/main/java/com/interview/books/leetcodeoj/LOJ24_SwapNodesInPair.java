package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午4:13
 */
public class LOJ24_SwapNodesInPair {
    public ListNode swapPairs(ListNode head) {
        if(head == null || head.next == null) return head;
        ListNode dummy = new ListNode(0);
        ListNode pre = dummy;
        ListNode cur = head;
        while(cur != null && cur.next != null){
            ListNode next = cur.next.next;
            pre.next = cur.next;
            cur.next.next = cur;
            cur.next = next;
            pre = cur;
            cur = next;
        }
        return dummy.next;
    }
}
