package com.interview.books.ccinterview;

import com.interview.leetcode.utils.ListNode;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-4
 * Time: 下午6:19
 */
public class CC4_LinkedListPalindrome {
    public static boolean isPalindrome(ListNode head){
        Stack<Integer> stack = new Stack<>();
        ListNode fast = head;
        ListNode slow = head;
        while(fast != null && fast.next != null){
            stack.push(slow.val);
            slow = slow.next;
            fast = fast.next.next;
        }
        if(fast != null) slow = slow.next; //for the list node number is odd;
        while(slow != null){
            if(slow.val != stack.pop()) return false;
            slow = slow.next;
        }
        return true;
    }

    public static void main(String[] args){
        int[] array = new int[]{1,2,3,4,3,2,1};
        ListNode head = ListNode.createList(array);
        System.out.println(isPalindrome(head));

        array = new int[]{1,2,3,4,4,3,2,1};
        head = ListNode.createList(array);
        System.out.println(isPalindrome(head));

        array = new int[]{1,2,3,4,4,5,3,2,1};
        head = ListNode.createList(array);
        System.out.println(isPalindrome(head));
    }
}
