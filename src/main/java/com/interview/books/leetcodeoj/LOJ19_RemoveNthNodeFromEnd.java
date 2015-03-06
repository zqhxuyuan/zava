package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午3:35
 */
public class LOJ19_RemoveNthNodeFromEnd {
    //use slow and fast pointer to scan the list, fast is n + 1 steps forward,
    //delete the next node of slow
    //be careful about n > len(list). if(fast == null && n > 0) return dummy.next;
    //use sample to verify the condition
    public ListNode removeNthFromEnd(ListNode head, int n) {
        if(head == null) return null;
        if(n <= 0) return head;
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode slow = dummy;
        ListNode fast = head;
        while(fast != null && n > 0){
            fast = fast.next;
            n--;
        }
        if(fast == null && n > 0) return dummy.next;
        while(fast != null){
            fast = fast.next;
            slow = slow.next;
        }
        if(slow.next != null) slow.next = slow.next.next;
        return dummy.next;
    }
}
