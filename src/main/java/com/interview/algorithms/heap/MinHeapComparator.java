package com.interview.algorithms.heap;

import java.util.Comparator;

/**
 * Created_By: zouzhile
 * Date: 10/26/14
 * Time: 3:23 PM
 */
public class MinHeapComparator implements Comparator<Integer> {

    @Override
    public int compare(Integer o1, Integer o2) {
        return o2 - o1;
    }
}
