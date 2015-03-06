package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.LinkedList;
import com.interview.basics.model.collection.list.Node;

/**
 * Created_By: stefanie
 * Date: 14-7-9
 * Time: 下午10:46
 *                 p1            p2
 *                 |             |
 *  a1,a2,a3,a4,a5,b1,b2,b3,b4,b5
 */
public class C3_7_ListRearrange {
    public static LinkedList<String> rearrange(LinkedList<String> list){
        Node<String> p1 = list.getHead();
        Node<String> p2 = list.getHead();

        while(p2 != null && p2.next != null){
            p1 = p1.next;
            p2 = p2.next.next;
        }

        p2 = list.getHead();
        while(true){
            Node<String> tmp1 = p2.next;
            p2.next = p1;
            if(p1.next == null) return list;
            Node<String> tmp2 = p1.next;
            p1.next = tmp1;
            p2 = tmp1;
            p1 = tmp2;
        }
    }
}
