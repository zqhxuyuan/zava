package com.interview.algorithms.dp;

/**
 * Given an int array, find the sub arrays sum is equals or closest smaller to a given K
 * @author stefanie
 *
 * Solution:
 * opt[i][k] saves 0~i element sum smaller but closest to K.
 *      opt[i][k] = max{ opt[i-1][k], opt[i-1][k-array[i]]+array[i] if k-array[i]>=0 }
 *
 * backtrace
 *      when not the first and opt[i][j] > opt[i-1][j] means i-th element is selected.
 *      when is the first element, if j = array[i], means i-th element is selected
 */
public class C12_27_SubArraysWithSumClosestToK {

    public static boolean[] find(int[] array, int K){
        int len = array.length;
        boolean[] mark = new boolean[len];

        //if K equals or larger than sum, return all the set
        int total = 0;
        for (int i = 0; i < len; i++) total += array[i];
        if(total <= K) {
            for(int i = 0; i < len; i++) mark[i] = true;
            return mark;
        }

        //opt[i][k] saves 0~i element sum closest to k.
        int[][] sums = new int[len][K + 1];
        for(int i = 0; i <= K; i++) sums[0][i] = 0;
        for (int i = 1; i < len; i++) {
            for(int k = 0; k < K + 1; k++){
                if(k >= array[i]){ //i-th element is smaller than j
                    //find a more close solution
                    sums[i][k] = Math.max(sums[i-1][k], sums[i-1][k-array[i]] + array[i]);
                } else
                    sums[i][k] = sums[i-1][k];
            }
        }

        //backtrace the solution
        int k = K;
        int i = len - 1;
        while(i >= 0 && k > 0){
            //when not the first and opt[i][j] > opt[i-1][j] means i-th element is selected.
            //when is the first element, if j = array[i], means i-th element is selected
            if(( i > 0 && sums[i][k] > sums[i-1][k]) || (i == 0 && k == array[i])){
                mark[i] = true;
                k -= array[i];
            }
            i--;
        }
        return mark;
    }

}
