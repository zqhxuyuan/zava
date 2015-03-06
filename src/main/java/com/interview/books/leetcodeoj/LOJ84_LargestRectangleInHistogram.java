package com.interview.books.leetcodeoj;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午9:11
 */
public class LOJ84_LargestRectangleInHistogram {
    /**
     * the area of [i,j] is the min(A[i]...A[j]) * (j - i + 1); how to optimize get min(A[i]...A[j]), use Stack
     * put index in Stack to calculate (j - i + 1) and put element in Stack in increasing sequence
     * the increasing sequence make sure: height = height[offset], width = i - stack.peek() - 1
     * when found a element not in increasing sequence, pop all the element in stack to keep increasing sequence.
     */
    //while(!stack.isEmpty() && (i == height.length || height[i] < height[stack.peek()])) pop and calculate
    //offset = stack.pop(); and int width = stack.isEmpty()? i : i - stack.peek() - 1; area = width * height[offset]
    //remember to push i in stack
    public int largestRectangleArea(int[] height) {
        if(height.length == 0) return 0;
        Stack<Integer> stack = new Stack();
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
