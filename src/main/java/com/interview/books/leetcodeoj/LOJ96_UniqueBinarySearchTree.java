package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 上午11:14
 */
public class LOJ96_UniqueBinarySearchTree {
    //catalan sequence
    //for every left = 0..left - 1; nums[total] += num[left] * num[total - 1 - left];
    public int numTrees(int n) {
        if(n == 0) return 0;
        if(n == 1) return 1;
        int[] nums = new int[n + 1];
        nums[0] = 1;
        nums[1] = 1;
        for(int total = 2; total <= n; total++){
            nums[total] = 0;
            for(int left = 0; left < total; left++){
                nums[total] += nums[left] * nums[total - 1 - left]; //total - 1 - left is right
            }
        }
        return nums[n];
    }
}
