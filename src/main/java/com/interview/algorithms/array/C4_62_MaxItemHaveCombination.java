package com.interview.algorithms.array;

import com.interview.basics.sort.QuickSorter;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/18/14
 * Time: 3:49 PM
 */
public class C4_62_MaxItemHaveCombination {
    static QuickSorter<Integer> SORTER = new QuickSorter<Integer>();

    public static int find(Integer[] array){
        SORTER.sort(array);
        for(int i = array.length - 1; i > 0; i--){
            if(checkCombination(array, 0, i - 1, 0, array[i])) return array[i];
        }
        return -1;
    }

    private static boolean checkCombination(Integer[] array, int start, int end, int currentSum, int N){
        if(currentSum > N)
            return false;
        if(currentSum == N)
            return true;
        for(int i = start; i <= end; i++) {
            if(checkCombination(array, i+1, end, currentSum + array[i], N))
                return true;
        }
        return false;
    }
}
