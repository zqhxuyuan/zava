package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-25
 * Time: 下午6:40
 */
public class LOJ92_ReverseLinkedListII {
    //find the prev, and tail based on m and n
    //reverse nodes between prev.next and tail
    public ListNode reverseBetween(ListNode head, int m, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;
        while(m > 1){
            prev = prev.next;
            m--; n--;
        }
        ListNode tail = prev.next;
        while(n > 1){
            tail = tail.next;
            n--;
        }
        ListNode cur = prev.next;
        prev.next = tail;
        prev = tail.next;
        while(prev != tail){
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return dummy.next;
    }
}
