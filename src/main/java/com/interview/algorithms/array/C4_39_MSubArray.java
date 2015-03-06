package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-7-28
 * Time: 下午8:44
 *
 * Given an array, write code to divide the array into M sub array (find largest M), make sure the sum of all sub array are the same.
 *
 * Clue:
 *      1. M should 1 <= M <= N
 *      2. sum(array) mod M == 0
 * Solution:
 *      1. Loop on M, check if M conform to Clue 2
 *      2. Find the M division, if could find a solution return m.
 *
 */
public class C4_39_MSubArray {

    public static int find(int[] array){
        int sum = 0;
        for(int i = 0; i < array.length; i++) sum += array[i];

        for(int m = array.length; m >= 2; m--){
            if(sum % m != 0) continue;
            if(canDivide(array, sum, m)) return m;
        }
        return 1;
    }

    private static boolean canDivide(int[] array, int sum, int m){
        int groupSum = sum / m;
        int[] mark = new int[array.length];
        for(int i = 0; i < array.length; i++){
            if(array[i] > groupSum) return false;
        }
        for(int j = 1; j <= m; j++){
            if(!canDivide(array, groupSum, j, mark, 0, 0)) return false;
        }
        return true;
    }

    private static boolean canDivide(int[] array, int groupSum, int groupID, int[] mark, int currentSum, int start){
        for(int i = start; i < array.length; i++){
            if(mark[i] == 0 && currentSum + array[i] <= groupSum){
                mark[i] = groupID;
                if(currentSum + array[i] == groupSum) return true;
                if(canDivide(array, groupSum, groupID, mark, currentSum+array[i], i+1))  return true;
                else mark[i] = 0;
            }
        }
        return false;
    }
}
