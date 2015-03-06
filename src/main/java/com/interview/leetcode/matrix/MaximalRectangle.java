package com.interview.leetcode.matrix;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-11-15
 * Time: 下午3:51
 *
 * Given a 2D binary matrix filled with 0's and 1's, find the largest rectangle containing all ones and return its area.
 *
 * Tricks:
 *  1. settle right-up point, and find the max matrix we could get.
 *  2. when find max matrix, first find the max rows and max cols, then shrink if some rule (have 0) is broken.
 *  3. tracking max during the binarysearch.
 */
public class MaximalRectangle {

    //F[x][y] = 1 + F[x][y-1] if A[x][y] is 0 , else 0
    public static int maximalRectangle(char[][] matrix){
        if(matrix.length == 0) return 0;
        int n = matrix.length;
        int m = matrix[0].length;
        int[][] hisgram = new int[n][m];

        for(int j = 0; j < m; j++){
            hisgram[0][j] = matrix[0][j] == '0'? 0 : 1;
        }
        for(int i = 1; i < n; i++){
            for(int j = 0; j < m; j++){
                hisgram[i][j] = matrix[i][j] == '0'? 0 : hisgram[i - 1][j] + 1;
            }
        }

        int max = 0;
        for(int i = 0; i < n; i++){
            max = Math.max(max, largestRectangleArea(hisgram[i]));
        }
        return max;
    }

    public static int largestRectangleArea(int[] height) {
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
