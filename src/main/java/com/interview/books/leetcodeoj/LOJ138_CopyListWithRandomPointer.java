package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-27
 * Time: 下午4:13
 */
public class LOJ138_CopyListWithRandomPointer {
    //1. clone RandomListNode and insert after the node;
    //2. copy random by node.next.random = node.random.next;
    //3. split the list into old one and clone one;
    class RandomListNode {
        int label;
        RandomListNode next, random;
        public RandomListNode(int value){
            label = value;
        }
    }
    public RandomListNode copyRandomList(RandomListNode head) {
        if(head == null) return null;
        clone(head);
        copyRandom(head);
        return split(head);
    }

    private void clone(RandomListNode head){
        while(head != null){
            RandomListNode clone = new RandomListNode(head.label);
            clone.next = head.next;
            head.next = clone;
            head = clone.next;
        }
    }

    private void copyRandom(RandomListNode head){
        while(head != null){
            if(head.random != null) head.next.random = head.random.next;
            head = head.next.next;
        }
    }

    private RandomListNode split(RandomListNode head){
        RandomListNode dummy = new RandomListNode(0);
        RandomListNode prev = dummy;
        while(head != null){
            prev.next = head.next;
            prev = prev.next;
            head.next = prev.next;
            head = head.next;
        }
        return dummy.next;
    }
}
