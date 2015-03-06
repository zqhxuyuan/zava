package com.interview.leetcode.application;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-11-15
 * Time: 下午1:07
 *
 * LeetCode: https://oj.leetcode.com/problems/largest-rectangle-in-histogram/
 *
 * Given n non-negative integers representing the histogram's bar height where the width of each bar is 1,
 * find the area of largest rectangle in the histogram.
 *
 * Above is a histogram where width of each bar is 1, given height = [2,1,5,6,2,3].
 * The largest rectangle is shown in the shaded area, which has area = 10 unit.
 *
 * Solution
 *   1. the rectangle in [i,j] should be the min(i~j) * j - i + 1;
 *      how to simplify the way to binarysearch min(i~j)
 *   2. during the scan, when element is larger than prev, put in stack
 *      when found a element smaller, start to pop from stack
 *          area = pop element * i - stack.peek() - 1.    //i is current index. stack.peek() ~ i-th is equals or larger than the pop element (already be pop)
 *   it's O(N)
 *
 * Tricks:
 *  1. define how to calculate the variable needed. have a bruce-force solution, and than try to simplify the binarysearch
 *         how to simplify the way to binarysearch min(i~j)
 *  2. use the relative relation(bigger/smaller/min/max) of element to simplify.
 *         use boolean or stack or queue to store the prev element hold
 */
public class LargestRectangleInHisgram {

    public int largestRectangleArea(int[] height) {
        if(height.length == 0) return 0;
        Stack<Integer> stack = new Stack<>();
        int max = 0;
        for(int i = 0; i <= height.length; i++){
            while(!stack.isEmpty() && (i == height.length || height[i] < height[stack.peek()])){
                Integer offset = stack.pop();
                int width = stack.isEmpty()? i : i - stack.peek() - 1;
                max = Math.max(max, width * height[offset]);
            }
            stack.push(i);
        }
        return max;
    }
}
