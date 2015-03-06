package com.interview.algorithms.list;

import com.interview.datastructures.list.Node;
import com.interview.utils.DataStructureUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Given two linked lists A and B, return a list C containing the intersection elements of A and B. The nodes of C should appear in the order as in B.
 *
 * Created_By: zouzhile
 * Date: 8/25/13
 * Time: 4:52 PM
 */
public class C3_2_InsersectionOfLists {

    public static Node findIntersection(Node list1, Node list2) {
        Set<String> table = new HashSet<String>();
        while(list1 != null) {
            table.add(list1.getValue());
            list1 = list1.next();
        }

        Node result = null, current = null;
        while(list2 != null) {
            if(table.contains(list2.getValue())) {
                Node clone = new Node(list2.getValue(), null);
                if (current == null)
                    result = current = clone;
                else {
                    current.setNext(clone);
                    current = clone;
                }
            }

            list2 = list2.next();
        }

        return result;

    }
    public static void main(String[] args) {
        Node list1 = DataStructureUtil.createList(new String[] {"a", "b", "c", "d"});
        Node list2 = DataStructureUtil.createList(new String[] {"d", "c", "e", "f"});
        Node result = findIntersection(list1, list2);
        System.out.print("The interaction of the 2 lists : ");
        while(result != null) {
            System.out.print(result.getValue() + "\t");
            result = result.next();
        }
    }
}
