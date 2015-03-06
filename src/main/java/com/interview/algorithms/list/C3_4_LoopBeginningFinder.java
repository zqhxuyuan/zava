package com.interview.algorithms.list;

import com.interview.datastructures.list.Node;

/**
 * Given a circular linked list, implement an algorithm which returns the node at the beginning of the loop.
 *
 * The solution
 *    1) Give two pointers, the fast pointer and slow pointer
 *    2) Move fast pointer at rate of 2 steps and slow pointer at rate of 1 step.
 *    3) When they collide, they will collide at LoopSize - K node after the loop beginning (or K node before the beginning) inside the loop.
 *       K = k mod LoopSize, where k is the length of the list outside the loop.
 *    4) Keep fast runner as is and put slow running to list head
 *    5) Move fast runner and slow runner both at rate of 1, they will collide at the loop beginning.
 *    Prove:
 *       straight line size: N, loop size: M, fast and slow point meet at K in loop
 *             => N + K = n * M
 *       so: N = n * M - K
 *
 * Created_By: zouzhile
 * Date: 8/30/13
 * Time: 7:48 PM
 */
public class C3_4_LoopBeginningFinder {
    public static Node findBegin(Node head){
        if(head == null) return null;
        Node fast = head.next();
        Node slow = head;
        while(fast != null && fast.next() != null &&fast != slow){
            fast = fast.next().next();
            slow = slow.next();
        }
        if(fast != slow) return null;
        slow = slow.next();  //due to at the init stage fast is one step ahead.
        fast = head;
        while(fast != slow){
            fast = fast.next();
            slow = slow.next();
        }
        return fast;
    }

    public static Node findLoopBeginning(Node head) {

        Node fastRunner = head;
        Node slowRunner = head;
        boolean isFreshStart = true;

        // first time collide, they will meet at K step before the loop beginning node.
        while (isFreshStart || fastRunner != slowRunner) {
            isFreshStart = false;
            fastRunner = fastRunner.next().next();
            slowRunner = slowRunner.next();
        }

        // move slow running back to head. Move 1 step for both runners, they will meet at loop beginning.
        slowRunner = head;
        isFreshStart = true;
        while (isFreshStart || fastRunner != slowRunner) {
            isFreshStart = false;
            fastRunner = fastRunner.next();
            slowRunner = slowRunner.next();
        }

        return fastRunner;
    }
}
