package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-7-25
 * Time: 下午11:04
 */
public class C4_36_SumMax2Array {
    public static int find(int[][] array){
        int maxSum = Integer.MIN_VALUE;
        for(int i = 0; i < array.length - 1; i++){
            for(int j = 0; j < array[0].length -1; j++){
                int sum = array[i][j] + array[i][j+1] + array[i+1][j] + array[i+1][j+1];
                if(sum > maxSum) maxSum = sum;
            }
        }
        return maxSum;
    }
}
