package com.interview.algorithms.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-6
 * Time: 下午4:07
 */
public class C1_76B_CombinationWithKElement {

    public static List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> combinations = new ArrayList<>();
        Integer[] current = new Integer[k];
        combine(n, k - 1, current, combinations);
        return combinations;
    }

    private static void combine(int n, int k, Integer[] current, List<List<Integer>> combinations){
        if(k < 0){
            combinations.add(new ArrayList<>(Arrays.asList(current)));
            return;
        }
        if(n < 1) return;
        current[k] = n;
        combine(n - 1, k - 1, current, combinations);
        combine(n - 1, k, current, combinations);
    }
}
