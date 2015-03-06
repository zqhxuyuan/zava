package com.interview.algorithms.general;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-6
 * Time: 下午3:51
 */
public class C1_76A_AllCombinations {

    public static List<List<Integer>> combine(int n) {
        List<List<Integer>> combinations = new ArrayList<>();
        Integer[] current = new Integer[n];
        combine(n, n - 1, current, combinations);
        return combinations;
    }

    private static void combine(int n, int k, Integer[] current, List<List<Integer>> combinations){
        current[k] = n;
        combinations.add(createOne(k, current));
        if(n > 1){
            combine(n - 1, k - 1, current, combinations);
            combine(n - 1, k, current, combinations);
        }
    }

    private static List<Integer> createOne(int k, Integer[] current){
        List<Integer> combination = new ArrayList<>();
        for(int i = k; i < current.length; i++) combination.add(current[i]);
        return combination;
    }
}
