package com.interview.leetcode.list;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-11-20
 * Time: 下午6:11
 */
public class ListSort {
    static class InsertSort{
        public static ListNode sort(ListNode head) {
            if(head == null || head.next == null) return head;
            ListNode cur = head.next;
            head.next = null;
            while(cur != null){
                ListNode next = cur.next;
                head = insertNode(head, cur);
                cur = next;
            }
            return head;
        }

        public static ListNode insertNode(ListNode head, ListNode node){
            ListNode dummyNode = new ListNode(0);
            dummyNode.next = head;
            head = dummyNode;
            while(head.next != null && head.next.val <= node.val){
                head = head.next;
            }
            node.next = head.next;
            head.next = node;
            return dummyNode.next;
        }
    }

    static class MergeSort{
        public static ListNode sort(ListNode head){
            int length = length(head);
            return sort(head, length);
        }

        private static ListNode sort(ListNode head, int length){
            if (length == 1) {
                head.next = null;
                return head;
            }
            ListNode mid = head;
            for (int i = 0; i < length / 2; i++)   mid = mid.next;
            head = sort(head, length / 2);
            mid = sort(mid, length - length / 2);
            return merge(head, mid);
        }

        public static ListNode merge(ListNode l1, ListNode l2){
            ListNode dummyHead = new ListNode(0);
            ListNode prev = dummyHead;
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
            if(l1 == null) prev.next = l2;
            else prev.next = l1;
            return dummyHead.next;
        }

        public static int length(ListNode head){
            int length = 0;
            while(head != null){
                head = head.next;
                length++;
            }
            return length;
        }

    }
}
