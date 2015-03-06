package com.interview.books.question300;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 15-1-25
 * Time: 下午10:31
 */
public class TQ68_MaxElementHaveCombination {

    public int maxElement(int[] array){
        Arrays.sort(array);
        for(int i = array.length - 1; i > 0; i--){
            if(canCombine(array, 0, i - 1, 0, array[i])) return array[i];
        }
        return -1;
    }

    private boolean canCombine(int[] array, int start, int end, int currentSum, int target){
        if(currentSum > target) return false;
        if(currentSum == target) return true;
        for(int i = start; i <= end; i++) {
            if(canCombine(array, i + 1, end, currentSum + array[i], target)) return true;
        }
        return false;
    }

    public static void main(String[] args){
        TQ68_MaxElementHaveCombination finder = new TQ68_MaxElementHaveCombination();
        int[] array = new int[]{2,3,7,10,14};
        System.out.println(finder.maxElement(array)); //10
    }
}
