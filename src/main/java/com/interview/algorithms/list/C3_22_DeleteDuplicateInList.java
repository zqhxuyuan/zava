package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.Node;

/**
 * Created_By: stefanie
 * Date: 14-11-10
 * Time: 下午9:04
 */
public class C3_22_DeleteDuplicateInList {
    public static Node deleteDuplicates(Node head) {
        if(head == null || head.next == null) return head; //empty or single node list

        Node newHead = new Node(0); //for cases need change head
        newHead.next = head;

        Node prev = newHead;
        Node back = prev.next;
        Node front = back.next;
        while(true){
            while(front != null && front.item.equals(back.item)) front = front.next;
            if(back.next == front){ //no duplication found, need save back
                prev.next = back;
                prev = back;
            }
            if(front == null) break;
            back = front;
            front = back.next;
        }
        prev.next = null;  //prev is the last element need to save
        return newHead.next;
    }
}
