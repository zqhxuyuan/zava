package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.LinkedList;
import com.interview.basics.model.collection.list.Node;

/**
 * Created_By: stefanie
 * Date: 14-7-30
 * Time: 下午11:08
 */
public class C3_16_FirstCommonNode {

    public static Node find(LinkedList list1, LinkedList list2){
        if(list1.size() < list2.size()){
            LinkedList tmp = list1;
            list1 = list2;
            list2 = tmp;
        }

        int longer = list1.size();
        Node p1 = list1.getHead();
        Node p2 = list2.getHead();
        while(longer > list2.size() && !p1.item.equals(p2.item)){
            p1 = p1.next;
            longer--;
        }
        while(p1 != null && p2 != null && !p1.item.equals(p2.item)){
                p1 = p1.next;
                p2 = p2.next;
        }
        return p1;
    }
}
