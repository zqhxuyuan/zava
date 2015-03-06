package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;

import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午3:59
 */
public class LOJ23_MergeKSortedList {

    public ListNode mergeKLists(List<ListNode> lists) {
        if(lists == null || lists.size() == 0) return null;
        int end = lists.size() - 1;
        while(end > 0){
            int begin = 0;
            while(begin < end){
                lists.set(begin, merge2Lists(lists.get(begin), lists.get(end)));
                begin++;
                end--;
            }
        }
        return lists.get(0);
    }

    public ListNode merge2Lists(ListNode l1, ListNode l2){
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
}
