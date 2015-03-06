package com.interview.leetcode.list;

import com.interview.leetcode.utils.ListNode;

/**
 * Created_By: stefanie
 * Date: 14-11-15
 * Time: 下午7:08
 *
 * Given a singly linked list L: L0→L1→…→Ln-1→Ln,
 *
 * 1. reverse the linked list as Ln→Ln-1→...L1→L0
 *      reverse the whole list  {@link Reverser}
 *      reverse one part of the list, start at prev.next and end with tail   {@link Reverser}
 *      reverse one part of the list, from m ~ n  {@link Reverser}
 * 2. interleaving two linked list  {@link #interleaving}
 *      first   A0→A1→...An-1→An   second B0→B1→...Bn-1→Bn
 *      result  A0→B0→A1→B1→...An-1→Bn-1→An→Bn
 * 3. if a linked list have cycle and the begin node of the cycle {@link #hasCycle} {@link #detectCycle}
 * 4. partition list by a given target, node smaller go before, and larger or equals go after. {@link #partition}
 * 5. sort list: better using merge sort, constant space and O(nlgn)  move to {@link ListSort}
 *       use length to partition list into two half, remember to set the tail to null when return list (length == 1)
 * 6. reverse nodes in k-group.
 *
 * Tricks:
 *  1. two pointer: fast and slow
 *  2. recursive: use return value or prev node in the parameter, the length of the list
 *  3. be careful when do pointer change, create temp node if needed.
 *
 */
public class ListOperation {
    public static ListNode getMiddle(ListNode node){
        ListNode fast = node;
        ListNode slow = node;
        while(fast != null && fast.next != null){
            fast = fast.next.next;
            slow = slow.next;
        }
        return slow;
    }

    public static ListNode getMiddlePre(ListNode node){
        ListNode fast = node.next;
        ListNode slow = node;
        while(fast != null & fast.next != null){
            fast = fast.next.next;
            slow = slow.next;
        }
        return slow;
    }

    /**
     * create temp node for both next
     */
    public static void interleaving(ListNode first, ListNode second){
        while(first != null && second != null){
            ListNode firstNext = first.next;
            ListNode secondNext = second.next;
            first.next = second;
            if(firstNext == null) return;
            second.next = firstNext;
            first = firstNext;
            second = secondNext;
        }
    }

    public static ListNode interleavingMerge(ListNode l1, ListNode l2){
        int index = 0;
        ListNode dummyHead = new ListNode(0);
        ListNode prev = dummyHead;
        while (l1 != null && l2 != null) {
            if ((index & 1) == 0) {
                prev.next = l1;
                l1 = l1.next;
            } else {
                prev.next = l2;
                l2 = l2.next;
            }
            prev = prev.next;
            index++;
        }
        if (l1 != null) prev.next = l1;
        else prev.next = l2;
        return dummyHead.next;
    }

    /**
     * detect if the given list have cycle
     */
    public static boolean hasCycle(ListNode head) {
        if(head == null) return false;
        ListNode fast = head.next;
        ListNode slow = head;
        while(fast != null && fast.next != null &&fast != slow){
            fast = fast.next.next;
            slow = slow.next;
        }
        return fast == slow;
    }

    /**
     * when init fast as head.next, when fast and slow meet,
     * slow should go one step ahead, in order to meet fast at the begin point
     */
    public static ListNode detectCycle(ListNode head) {
        if(head == null) return null;
        ListNode fast = head.next;
        ListNode slow = head;
        while(fast != null && fast.next != null &&fast != slow){
            fast = fast.next.next;
            slow = slow.next;
        }
        if(fast != slow) return null;
        slow = slow.next;  //due to line 15, fast is one step ahead.
        fast = head;
        while(fast != slow){
            fast = fast.next;
            slow = slow.next;
        }
        return fast;
    }

    /**
     * Given two list, detect if they have intersection, be ware of if the list have cycle
     */
    public static boolean detectIntersection(ListNode l1, ListNode l2){
        ListNode hasCycle1 = detectCycle(l1);
        ListNode hasCycle2 = detectCycle(l2);

        if(hasCycle1 == null && hasCycle2 == null){
            while(l1.next != null) l1 = l1.next;
            while(l2.next != null) l2 = l2.next;
            return l1 == l2;
        } else if(hasCycle1 != null && hasCycle2 != null){
            ListNode p1 = hasCycle1.next;
            while(p1 != hasCycle1 && p1 != hasCycle2) p1 = p1.next;
            return p1 == hasCycle2;
        } else return false;
    }

    /**
     * insert a number in a sorted cyclic list
     */
    public static ListNode insert(ListNode head, int k){
        ListNode pre = head;
        ListNode cur = head.next;
        while(cur.val >= k && cur != head){
            cur = cur.next;
            pre = pre.next;
        }
        ListNode newNode = new ListNode(k);
        pre.next = newNode;
        newNode.next = cur;
        return k < head.val? newNode : head;
    }
    /**
     * create two dummy head small and large
     */
    public static ListNode partition(ListNode head, int x) {
        if(head == null) return head;
        ListNode smallDummy = new ListNode(0);
        ListNode small = smallDummy;
        ListNode largeDummy = new ListNode(0);
        ListNode large = largeDummy;
        while(head != null){
            if(head.val < x)   {
                small.next = head;
                small = small.next;
            } else {
                large.next = head;
                large = large.next;
            }
            head = head.next;
        }
        large.next = null;
        small.next = largeDummy.next;
        return smallDummy.next;
    }

    public static int length(ListNode head){
        int length = 0;
        while(head != null){
            head = head.next;
            length++;
        }
        return length;
    }

    /**
     * 1. find the mid and tail using fast and slow pointer moving
     * 2. reverse the node from mid.next ~ tail
     * 3. do interleaving head and mid.next
     *      remember to set mid.next = null to end the first list before run interleaving
     */
    public static void reorderList(ListNode head) {
        if(head == null || head.next == null) return;
        ListNode slow = head;
        ListNode fast = head.next;
        while(fast != null && fast.next != null){ //1->2->3->4  slow is pre of mid
            fast = fast.next.next;
            slow = slow.next;
        }

        ListNode mid = ListReverser.reverse(slow.next);
        slow.next = null;
        interleaving(head, mid);
    }

    /**
     * Remove duplicated node
     */
    public ListNode deleteDuplicatesOnce(ListNode head) {
        ListNode prev = head;
        while(prev != null && prev.next != null){
            if(prev.next.val != prev.val){
                prev = prev.next;
            } else {  //1->1->2
                prev.next = prev.next.next;    //1->2
            }
        }
        return head;
    }

    /**
     * Delete node appear more than once
     * @param head
     * @return
     */
    public ListNode deleteDuplicates(ListNode head) {
        ListNode dummyHead = new ListNode(0);
        dummyHead.next = head;
        ListNode prev = dummyHead;
        ListNode front = head;
        while(front != null){
            ListNode back = front.next;
            while(back != null && back.val == front.val) back = back.next;
            if(front.next == back){  //front is only appear once
                prev.next = front;
                prev = prev.next;
            }
            front = back;
        }
        prev.next = null;
        return dummyHead.next;
    }

    public static ListNode firstCommon(ListNode l1, ListNode l2){
        int len1 = length(l1);
        int len2 = length(l2);
        if(len1 < len2){
            ListNode tmp = l1;
            l1 = l2;
            l2 = tmp;
        }

        int longer = Math.max(len1, len2);
        int smaller = Math.min(len1, len2);
        while(longer > smaller && l1.val != l2.val){
            l1 = l1.next;
            longer--;
        }
        while(l1 != null && l2 != null && l1.val != l2.val){
            l1 = l1.next;
            l2 = l2.next;
        }
        return l1;
    }

}
