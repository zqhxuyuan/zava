package com.interview.books.leetcodeoj;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-12-27
 * Time: 下午12:32
 */
public class LOJ135_Candy {
    //scan forward and backward to adjust candy based on the rule
    //forward, i compare with i - 1, backward, i compare with i + 1
    //during backward scan, condition is (ratings[i] > ratings[i+1] && candy[i] <= candy[i+1])
    public int candy(int[] ratings) {
        if(ratings.length <= 1) return ratings.length;
        int[] candy = new int[ratings.length];
        Arrays.fill(candy, 1);
        for(int i = 1; i < candy.length; i++){
            if(ratings[i] > ratings[i-1]) candy[i] = candy[i-1] + 1;
        }
        for(int i = candy.length - 2; i >= 0; i--){
            if(ratings[i] > ratings[i+1] && candy[i] <= candy[i+1]) candy[i] = candy[i+1] + 1;
        }
        int count = 0;
        for(int i = 0; i < candy.length; i++) count += candy[i];
        return count;
    }
}
