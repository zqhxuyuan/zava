package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.LinkedList;
import com.interview.basics.model.collection.list.Node;

/**
 * Created_By: stefanie
 * Date: 14-7-19
 * Time: 上午12:28
 */
public class C3_13_ListIntersectionProver {

    public static boolean haveIntersection(LinkedList list1, LinkedList list2){
        Node hasCycle1 = C3_14_ListCycleFinder.hasCycle(list1);
        Node hasCycle2 = C3_14_ListCycleFinder.hasCycle(list2);

        if(hasCycle1 == null && hasCycle2 == null){
            Node end1 = list1.getHead();
            while(end1.next != null) end1 = end1.next;
            Node end2 = list2.getHead();
            while(end2.next != null) end2 = end2.next;
            return end1 == end2;
        } else if(hasCycle1 != null && hasCycle2 != null){
            Node p1 = hasCycle1.next;
            while(p1 != hasCycle1 && p1 != hasCycle2) p1 = p1.next;
            return p1 == hasCycle2;
        } else return false;
    }
}
