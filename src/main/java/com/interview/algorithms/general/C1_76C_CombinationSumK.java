package com.interview.algorithms.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-6
 * Time: 下午10:12
 */
public class C1_76C_CombinationSumK {

    public static List<List<Integer>> combinationSum(int[] candidates, int target, boolean canDuplicate) {
        Arrays.sort(candidates);
        List<List<Integer>> combinations = new ArrayList<>();
        Integer[] current = new Integer[target];
        if(canDuplicate)    combineWithDupication(candidates, 0, 0, current, 0, target, combinations);
        else                combine(candidates, 0, 0, current, 0, target, combinations);
        return combinations;
    }

    private static void combine(int[] num, int i, int j, Integer[] current, int sum, int target, List<List<Integer>> combinations){
        if(sum >= target) return;
        current[j] = num[i];
        if(sum + num[i] == target){
            combinations.add(createOne(j, current));
            return;
        }
        if(i < num.length - 1){  //no duplicate put in, always i + 1
            combine(num, i + 1, j + 1, current, sum + num[i], target, combinations);
            while(i < num.length - 1 && num[i + 1] == num[i]) i++;      //find the next different element to replace i
            if(i < num.length - 1)  combine(num, i + 1, j, current, sum, target, combinations);
        }
    }

    private static void combineWithDupication(int[] num, int i, int j, Integer[] current, int sum, int target, List<List<Integer>> combinations){
        if(sum >= target) return;
        if(j > current.length - 1) return;
        current[j] = num[i];
        if(sum + num[i] == target){
            combinations.add(createOne(j, current));
            return;
        }
        combineWithDupication(num, i, j + 1, current, sum + num[i], target, combinations);        //duplicate put in, still i
        if(i < num.length - 1){
            while(i < num.length - 1 && num[i + 1] == num[i]) i++;
            if(i < num.length - 1)  combineWithDupication(num, i + 1, j, current, sum, target, combinations);
        }
    }

    private static List<Integer> createOne(int k, Integer[] current){
        List<Integer> combination = new ArrayList<>();
        for(int i = 0; i <= k; i++) combination.add(current[i]);
        return combination;
    }
}
