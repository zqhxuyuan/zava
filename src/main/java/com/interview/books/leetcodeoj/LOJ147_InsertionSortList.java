package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-28
 * Time: 下午2:06
 */
public class LOJ147_InsertionSortList {
    //scan from the list, insert i-th node to the right place among 0 - (i-1)th nodes.
    //use dummy node to avoid head change
    //loop insert node from head.next, and set previous 0-(i-1)th nodes end with null.
    public ListNode insertionSortList(ListNode head) {
        if(head == null || head.next == null) return head;
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode cur = head.next;
        head.next = null;
        while(cur != null){
            ListNode next = cur.next;
            insertNode(dummy, cur);
            cur = next;
        }
        return dummy.next;
    }

    public void insertNode(ListNode head, ListNode node){
        while(head.next != null && head.next.val <= node.val){
            head = head.next;
        }
        node.next = head.next;
        head.next = node;
    }
}
