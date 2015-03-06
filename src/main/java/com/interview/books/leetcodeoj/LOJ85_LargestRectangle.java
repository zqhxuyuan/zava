package com.interview.books.leetcodeoj;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午9:30
 */
public class LOJ85_LargestRectangle {
    //use largestRectangleArea() method, loop every row to calculate histogram
    //matrix is char[][], so need check matrix[i][j] == '0'
    //Time: O(N^2), Space: O(N)
    public int maximalRectangle(char[][] matrix){
        if(matrix.length == 0) return 0;
        int max = 0;
        int[] histogram = new int[matrix[0].length];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                if(matrix[i][j] == '0') histogram[j] = 0;
                else histogram[j]++;
            }
            max = Math.max(max, largestRectangleArea(histogram));
        }
        return max;
    }

    //Time: O(N), Space O(N)
    public int largestRectangleArea(int[] height) {
        Stack<Integer> stack = new Stack();
        int max = 0;
        for(int i = 0; i <= height.length; i++){
            while(!stack.isEmpty() && (i == height.length || height[i] < height[stack.peek()])){
                int offset = stack.pop();
                int width = stack.isEmpty()? i : i - stack.peek() - 1;
                max = Math.max(max, width * height[offset]);
            }
            stack.push(i);
        }
        return max;
    }

    public static void main(String[] args){
        char[][] matrix = new char[][]{
                {'0'},
        };
        LOJ85_LargestRectangle finder = new LOJ85_LargestRectangle();
        System.out.println(finder.maximalRectangle(matrix));
    }
}
