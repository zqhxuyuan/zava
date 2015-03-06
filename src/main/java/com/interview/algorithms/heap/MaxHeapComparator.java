package com.interview.algorithms.heap;

import java.util.Comparator;

/**
 * Created_By: zouzhile
 * Date: 10/26/14
 * Time: 3:17 PM
 */
public class MaxHeapComparator implements Comparator<Integer> {

    @Override
    public int compare(Integer o1, Integer o2) {
        return o1 - o2;
    }
}
