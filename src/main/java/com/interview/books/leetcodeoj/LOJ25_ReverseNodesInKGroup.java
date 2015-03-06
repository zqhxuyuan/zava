package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午4:33
 */
public class LOJ25_ReverseNodesInKGroup {
    //1.remember to set dummy.next = head, when create a dummyHead
    //2.count start from 1 and count < k
    //3.when tail != null do the reverse, and pre = cur, tail = pre.next, count = 1;
    public ListNode reverseKGroup(ListNode head, int k) {
        if(head == null || k == 1) return head;
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode pre = dummy;
        ListNode tail = head;
        int count = 1;
        while(tail != null){
            count = 1;
            while(tail != null && count < k){
                tail = tail.next;
                count++;
            }
            if(tail != null){
                ListNode cur = pre.next;
                reverse(cur, tail, pre);
                pre = cur;
                tail = pre.next;
            }
        }
        return dummy.next;
    }

    public void reverse(ListNode node, ListNode tail, ListNode prev){
        if(node == tail) return;
        ListNode newHead = tail.next;
        while(newHead != tail){
            ListNode next = node.next;
            node.next = newHead;
            newHead = node;
            node = next;
        }
        prev.next = newHead;
    }

    public static void main(String[] args){
        LOJ25_ReverseNodesInKGroup reverser = new LOJ25_ReverseNodesInKGroup();
        ListNode head = new ListNode(1);
        head = reverser.reverseKGroup(head, 1);
        ListNode.print(head);
    }
}
