package com.interview.books.leetcodeoj;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午3:05
 */
public class LOJ16_ThreeSumClosest {
    //tracking closest and return closest + target
    //1. i, j, k is offset, not elements.
    public int threeSumClosest(int[] num, int target) {
        int closest = Integer.MAX_VALUE;
        Arrays.sort(num);
        for(int i = 0; i < num.length - 2; i++){
            int j = i + 1;
            int k = num.length - 1;
            while(j < k){
                int sum = num[i] + num[j] + num[k];
                if(sum == target) return target;
                int diff = sum - target;
                if(Math.abs(diff) < Math.abs(closest)) closest = diff;
                if(sum > target) k--;
                else j++;
            }
        }
        return closest + target;
    }
}
