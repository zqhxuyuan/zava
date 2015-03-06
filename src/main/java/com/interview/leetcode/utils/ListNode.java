package com.interview.leetcode.utils;

/**
 * Created_By: stefanie
 * Date: 14-11-13
 * Time: 下午1:41
 */
public class ListNode {
    public int val;
    public ListNode next;

    public ListNode(int val) {
        this.val = val;
    }

    public static void print(ListNode node){
        while(node != null){
            System.out.print(node.val + ", ");
            node = node.next;
        }
        System.out.println();
    }

    public static ListNode createList(int[] array) {
        if(array.length <= 0) return null;
        ListNode head = new ListNode(array[0]);
        ListNode prev = head;
        for (int i = 1; i < array.length; i++) {
            ListNode next = new ListNode(array[i]);
            prev.next = next;
            prev = next;
        }
        return head;
    }
}
