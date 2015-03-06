package com.interview.books.question300;

import com.interview.utils.ArrayUtil;

/**
 * Created by stefanie on 1/21/15.
 */
public class TQ56_ArrayDivision {
    public boolean[] division(int[] array){
        int sum = ArrayUtil.sum(array, 0, array.length - 1);
        return find(array, sum / 2);
    }
    
    //state: sums[i][j] is the sum of subset of (0~ith element) closest to j.
    //initialize: sum[0][*] = 0;
    //function: sum[i][j] = 
    //              Math.max(sum[i-1][j], sum[i-1][j-array[i]] + array[i]) if j >= array[i]
    //              sun[i-1][j] if j > array[i]
    //result: sum[n-1][target]
    //backtracing: if(( i > 0 && sums[i][k] > sums[i-1][k]) || (i == 0 && k == array[i]))  mark[i] = true and k -= array[i]
    private boolean[] find(int[] array, int target){
        int len = array.length;
        boolean[] mark = new boolean[len];

        //if target equals or larger than sum, return all the set
        int total = 0;
        for (int i = 0; i < len; i++) total += array[i];
        if(total <= target) {
            for(int i = 0; i < len; i++) mark[i] = true;
            return mark;
        }

        //opt[i][j] saves 0~i element sum closest to j.
        int[][] sums = new int[len][target + 1];
        for(int i = 0; i <= target; i++) sums[0][i] = 0;
        for (int i = 1; i < len; i++) {
            for(int j = 0; j < target + 1; j++){
                if(j >= array[i]){ //i-th element is smaller than j
                    //find a more close solution
                    sums[i][j] = Math.max(sums[i-1][j], sums[i-1][j-array[i]] + array[i]);
                } else
                    sums[i][j] = sums[i-1][j];
            }
        }

        //backtrace the solution
        int k = target;
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
