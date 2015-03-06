package com.interview.leetcode.list;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-11-15
 * Time: 下午7:45
 */
public class RandomNodeList {

    static class RandomListNode{
        int label;
        RandomListNode next;
        RandomListNode random;
        public RandomListNode(int val){
            this.label = val;
        }
    }

    //Time: O(N), Space: O(N)
    public RandomListNode copyRandomListMap(RandomListNode head) {
        HashMap<RandomListNode, RandomListNode> nodeMap = new HashMap<>();

        RandomListNode p = head;
        RandomListNode dummyHead = new RandomListNode(0);
        RandomListNode prev = dummyHead;
        while(p != null){  //copy the list and save node in nodeMap
            prev.next = new RandomListNode(p.label);
            nodeMap.put(p, prev.next);
            prev = prev.next;
            p = p.next;
        }
        p = head;
        prev = dummyHead.next;
        while(p != null){ //copy the random pointer using nodeMap
            if(p.random != null)  prev.random = nodeMap.get(p.random);
            p = p.next;
            prev = prev.next;
        }
        return dummyHead.next;
    }

    /**
     * Modify the original list: the next node of the original node to point to its own copy.
     *      node.next.random = node.random.next;  //copy.random = random.copy
     * Step:
     *   1. copy list and interleaving them
     *   2. copy random
     *   3. split copy and origin list
     */
    //Time: O(N), Space: O(1)
    public static RandomListNode copyRandomList(RandomListNode head) {
        if (head == null) {
            return null;
        }
        copyNext(head);
        copyRandom(head);
        return splitList(head);
    }

    private static void copyNext(RandomListNode head) {
        while (head != null) {
            RandomListNode newNode = new RandomListNode(head.label);
            newNode.random = head.random;
            newNode.next = head.next;
            head.next = newNode;
            head = head.next.next;
        }
    }

    private static void copyRandom(RandomListNode head) {
        while (head != null) {
            if (head.next.random != null) {
                head.next.random = head.random.next;
            }
            head = head.next.next;
        }
    }

    private static RandomListNode splitList(RandomListNode head) {
        RandomListNode copyHead = head.next;
        while (head != null) {
            RandomListNode temp = head.next;
            head.next = temp.next;
            head = head.next;
            if (temp.next != null) {
                temp.next = temp.next.next;
            }
        }
        return copyHead;
    }

}