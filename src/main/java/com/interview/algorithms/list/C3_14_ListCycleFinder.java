package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.LinkedList;
import com.interview.basics.model.collection.list.Node;

/**
 * Created_By: stefanie
 * Date: 14-7-19
 * Time: 上午12:30
 */
public class C3_14_ListCycleFinder {

    public static Node hasCycle(LinkedList list){
        Node p1 = list.getHead();
        Node p2 = list.getHead();
        boolean isFirst = true;
        while(isFirst || (p1 != p2 && p2 != null && p2.next != null)){
            p1 = p1.next;
            p2 = p2.next.next;
            if(isFirst) isFirst = false;
        }
        if(!isFirst && p1 == p2) return p1;
        else return null;
    }
}
