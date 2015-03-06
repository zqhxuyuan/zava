package com.interview.utils;

import com.interview.datastructures.list.Node;

/**
 * Created_By: zouzhile
 * Date: 8/25/13
 * Time: 5:21 PM
 */
public class DataStructureUtil {

    public static Node createList(String[] values) {
        if (values.length == 0)
            return null;
        Node result = new Node(values[0], null);
        Node current = result;
        for(int i = 1; i < values.length; i ++ ) {
            Node node = new Node(values[i], null);
            current.setNext(node);
            current = node;
        }

        return result;
    }
}
