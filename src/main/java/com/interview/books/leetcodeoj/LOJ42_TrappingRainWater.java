package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-21
 * Time: 下午5:50
 */
public class LOJ42_TrappingRainWater {
    //find the max index, and scan from left and right to center, tracking blocks and increasing total
    //when A[i] < A[left] or A[i] < A[right], blocks += A[i];
    //when find another boundry, total += A[left] * (i - left - 1) - blocks; remember to set blocks = 0;
    public int trap(int[] A) {
        if(A.length < 2) return 0;
        int maxIdx = maxIdx(A);

        int total = 0;
        int left = 0; int blocks = 0;
        for(int i = 1; i <= maxIdx; i++){
            if(A[i] < A[left])  blocks += A[i];
            else {
                total += A[left] * (i - left - 1) - blocks;
                blocks = 0; left = i;
            }
        }
        int right = A.length - 1; blocks = 0;
        for(int i = A.length - 2; i >= maxIdx; i--){
            if(A[i] < A[right]) blocks += A[i];
            else {
                total += A[right] * (right - i - 1) - blocks;
                blocks = 0; right = i;
            }
        }
        return total;
    }

    private int maxIdx(int[] A){
        int maxIdx = 0;
        for(int i = 1; i < A.length; i++){
            if(A[i] > A[maxIdx]) maxIdx = i;
        }
        return maxIdx;
    }

    public static void main(String[] args){
        int[] nums = new int[]{5,4,1,2};
        LOJ42_TrappingRainWater container = new LOJ42_TrappingRainWater();
        System.out.println(container.trap(nums));
    }
}
