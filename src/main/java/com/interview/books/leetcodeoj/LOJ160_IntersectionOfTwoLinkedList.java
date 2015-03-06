package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-12-29
 * Time: 下午10:37
 */
public class LOJ160_IntersectionOfTwoLinkedList {
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        int lenA = length(headA);
        int lenB = length(headB);
        if(lenA == 0 || lenB == 0) return null;
        if(lenA < lenB){  //keep headA as the longer one
            ListNode temp = headA;
            headA = headB;
            headB = temp;
            int lenTmp = lenA;
            lenA = lenB;
            lenB = lenTmp;
        }
        while(lenA > lenB && headA != headB) {
            headA = headA.next;
            lenA--;
        }
        if(headA == headB) return headA;
        while(lenA > 0 && headA != headB){
            headA = headA.next;
            headB = headB.next;
            lenA--;
            lenB--;
        }
        return lenA > 0? headA : null;
    }

    public int length(ListNode head){
        int length = 0;
        while(head != null){
            head = head.next;
            length++;
        }
        return length;
    }
}
