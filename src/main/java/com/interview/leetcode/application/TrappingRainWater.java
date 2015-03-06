package com.interview.leetcode.application;

/**
 * Created_By: stefanie
 * Date: 14-11-15
 * Time: 下午2:44
 *
 * https://oj.leetcode.com/problems/trapping-rain-water/
 *
 * Given n non-negative integers representing an elevation map where the width of each bar is 1,
 * compute how much water it is able to trap after raining.
 * For example, Given [0,1,0,2,1,0,1,3,2,1,2,1], return 6.
 *
 * Sol:
 *  1. find the highest elevation
 *  2. scan from left to highest, and right to highest, if found a range could trap water,
 *     next equals or higher one than prev one, calculate how many water could trap.
 *  3. during calculate, cal the total area, and delete the blocked area.
 *
 * Tricks:
 *  1. the area(i, j) = min(i, j) * (j-i+1) - blocks(i+1 ~j-1)
 *  2. Simply the binarysearch from find the max.
 *  3. Scan from left/right to center/max.
 */
public class TrappingRainWater {

    public int trap(int[] A) {
        if(A.length < 2) return 0;

        int amout = 0;
        int max = max(A); //find the highest elevation

        int cur = 0;
        while(cur < max){ //scan from left to highest
            int higher = cur + 1;
            while(higher <= max && A[higher] < A[cur]) higher++; //found the next equals or higher one;
            amout += (higher - 1 - cur) * Math.min(A[cur], A[higher]); //add the total area.
            for(int i = cur + 1; i < higher; i++) amout -= A[i]; //delete the blocked area.
            cur = higher;
        }
        cur = A.length - 1;
        while(cur > max){ //scan from right to highest
            int higher = cur - 1;
            while(higher >= max && A[higher] < A[cur]) higher--; //found the next equals or higher one;
            amout += (cur - 1 - higher) * Math.min(A[cur], A[higher]); //add the total area.
            for(int i = cur - 1; i > higher; i--) amout -= A[i]; //delete the blocked area.
            cur = higher;
        }
        return amout;
    }

    private int max(int[] A){
        int max = 0;
        for(int i = 1; i < A.length; i++){
            if(A[i] > A[max]) max = i;
        }
        return max;
    }
}
