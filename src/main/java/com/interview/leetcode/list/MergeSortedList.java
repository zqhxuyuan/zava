package com.interview.leetcode.list;

import com.interview.leetcode.utils.ListNode;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created_By: stefanie
 * Date: 14-11-13
 * Time: 下午2:16
 *
 * Merge sorted list into one list array
 *
 * 1. merge 2 sorted list into one  {@link #merge2}
 * 2. merge k sorted list into one  {@link #mergek}
 *
 * Basic Tricks:
 * 1. Create a fake head to make code shorter and clearer
 * 2. Using Heap to get O(lgn)
 * 3. Don't forget the case if one list went to the end
 */
public class MergeSortedList {
    static Comparator<ListNode> COMPARATOR = new Comparator<ListNode>() {
        @Override
        public int compare(ListNode o1, ListNode o2) {
            if(o1 == null) return 1;
            else if(o2 == null) return -1;
            else return o1.val - o2.val;
        }
    };

    //Time: O(K), Space O(1)
    public static ListNode merge2(ListNode a, ListNode b){
        ListNode head = new ListNode(0);
        ListNode prev = head;
        while(a != null && b != null){
            if(a.val < b.val){
                prev.next = a;
                a = a.next;
            } else {
                prev.next = b;
                b = b.next;
            }
            prev = prev.next;
        }
        if(a == null) prev.next = b;
        else if(b == null) prev.next = a;
        return head.next;
    }

    //Time: O(NKlgK) Space O(1)
    public ListNode mergekOptz(List<ListNode> lists) {
        if (lists.size() == 0) return null;
        int end = lists.size() - 1;
        while (end > 0) {
            int begin = 0;
            while (begin < end) {
                lists.set(begin, merge2(lists.get(begin), lists.get(end)));
                begin++;
                end--;
            }
        }
        return lists.get(0);
    }

    //Time: O(NKlgK) Space:O(K)
    public static ListNode mergek(List<ListNode> lists){
        if(lists == null || lists.size() == 0) return null;

        PriorityQueue<ListNode> heap = new PriorityQueue<>(lists.size(), COMPARATOR);
        for(ListNode node : lists){
            if(node != null) heap.add(node);
        }

        ListNode head = new ListNode(0);
        ListNode prev = head;
        while(heap.size() > 0){
            ListNode min = heap.poll();
            prev.next = min;
            prev = min;
            if(min.next != null) heap.add(min.next);
        }
        return head.next;
    }
}
