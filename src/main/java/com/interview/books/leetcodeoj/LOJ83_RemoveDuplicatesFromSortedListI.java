package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午9:10
 */
public class LOJ83_RemoveDuplicatesFromSortedListI {
    public ListNode deleteDuplicates(ListNode head) {
        ListNode prev = head;
        ListNode cur = head;
        while(cur != null){
            while(cur != null && cur.val == prev.val) cur = cur.next;
            prev.next = cur;
            prev = prev.next;
        }
        return head;
    }
}
