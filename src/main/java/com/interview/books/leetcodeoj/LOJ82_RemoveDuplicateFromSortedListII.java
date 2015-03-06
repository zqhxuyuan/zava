package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午9:06
 */
public class LOJ82_RemoveDuplicateFromSortedListII {
    //use three pointer: prev, front and back.
    //while(back != null && back.val == front.val) back = back.next;
    //if(front.next == back) prev.next = front; prev = prev.next;
    //set prev.next = null at the end.
    public ListNode deleteDuplicates(ListNode head) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;
        ListNode front = head;
        ListNode back = head;
        while(front != null){
            back = front.next;
            while(back != null && back.val == front.val) back = back.next;
            if(front.next == back){
                prev.next = front;
                prev = prev.next;
            }
            front = back;
        }
        prev.next = front;
        return dummy.next;
    }
}
