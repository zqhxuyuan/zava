package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-28
 * Time: 下午2:18
 */
public class LOJ148_SortList {
    //use length to partition the list
    //  1. find the mid by for(int i = 0; i < length/2; i++)  mid = mid.next;
    //  2. mergesort the first half:  head = mergesort(head, length/2);
    //  3. mergesort the second half: mid = mergesort(mid, length - length/2);
    //  4. merge the two sorted list using dummy node;
    //important: when length == 1, set head.next = null and return head
    public ListNode sortList(ListNode head) {
        if(head == null || head.next == null) return head;
        int length = length(head);
        return mergesort(head, length);
    }

    private ListNode mergesort(ListNode head, int length){
        if(length == 1) {
            head.next = null;
            return head;
        }
        ListNode mid = head;
        for(int i = 0; i < length/2; i++)  mid = mid.next;
        head = mergesort(head, length/2);
        mid = mergesort(mid, length - length/2);
        return merge(head, mid);
    }

    private ListNode merge(ListNode l1, ListNode l2){
        ListNode dummy = new ListNode(0);
        ListNode prev = dummy;
        while(l1 != null && l2 != null){
            if(l1.val < l2.val){
                prev.next = l1;
                l1 = l1.next;
            } else {
                prev.next = l2;
                l2 = l2.next;
            }
            prev = prev.next;
        }
        prev.next = l1 != null? l1 : l2;
        return dummy.next;
    }

    private int length(ListNode head){
        int length = 0;
        while(head != null){
            head = head.next;
            length++;
        }
        return length;
    }
}
