package com.interview.books.leetcodeoj;

import com.interview.leetcode.utils.ListNode;
import com.interview.leetcode.utils.TreeNode;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午3:10
 */
public class LOJ109_ConvertSortedListToBST {
    //base in-order traversal to build a tree.
    //use current to tracking visited node in list, and length to tracking when to return.
    //left part is length/2, the right part is length - 1 - length/2;
    ListNode current;
    public TreeNode sortedListToBST(ListNode head) {
        if(head == null) return null;
        current = head;
        int length = length(head);
        return buildTree(length);
    }

    public int length(ListNode head){
        int length = 0;
        while(head != null){
            length++;
            head = head.next;
        }
        return length;
    }

    public TreeNode buildTree(int length){
        if(length == 0) return null;
        TreeNode left = buildTree(length / 2);
        TreeNode node = new TreeNode(current.val);
        current = current.next;
        node.left = left;
        node.right = buildTree(length - 1 - length/2);
        return node;
    }
}
