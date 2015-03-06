package com.interview.leetcode.dp;

import java.util.List;
import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-11-24
 * Time: 下午9:23
 */
public class MatrixDP {
    /**
     * You are climbing a stair case. It takes n steps to reach to the top.
     */
    //Time: O(N), Space: O(1)
    public int climbStairs(int n) {
        int[] ways = new int[3];
        ways[0] = 1;
        ways[1] = 2;
        for(int i = 2; i < n; i++){
            ways[i%3] = ways[(i - 2)%3] + ways[(i - 1)%3];
        }
        return ways[(n - 1)%3];
    }

    /**
     * Given a grid, and some obstacles are added.
     * How many unique paths would there be from 0.0 to m.n?
     */
    public int uniquePathsWithObstacles(int[][] grid) {
        if(grid.length == 0) return 0;
        int m = grid.length;
        int n = grid[0].length;
        int[][] path = new int[m][n];
        path[0][0] = grid[0][0] == 1? 0 : 1;
        for(int i = 1; i < m; i++) path[i][0] = grid[i][0] == 1? 0 : path[i - 1][0];
        for(int i = 1; i < n; i++) path[0][i] = grid[0][i] == 1? 0 : path[0][i - 1];
        for(int i = 1; i < m; i++){
            for(int j = 1; j < n; j++){
                path[i][j] = grid[i][j] == 1? 0 : path[i - 1][j] + path[i][j - 1];
            }
        }
        return path[m - 1][n - 1];
    }

    /**
     * Given a triangle of int numbers. Find the min path from the top to the bottom
     *  State: path[i][j] the path sum of j-th element from 0 layer to i-th layer
     *  Transfer: path[i][j] = Math.min(path[i-1][j], path[i-1][j-1])
     *  Init: path[0][0] = element in layer 0
     *  Result: the min path in layer n
     */
    public int minPathInTriangle(List<List<Integer>> triangle) {
        if(triangle == null || triangle.size() == 0) return 0;
        int layer = triangle.size();
        int[] path = new int[layer];
        path[0] = triangle.get(0).get(0);
        for(int i = 1; i < layer; i++){
            List<Integer> current = triangle.get(i);
            for(int j = current.size() - 1; j >= 0; j--){
                if(j == current.size() - 1) path[j] = path[j - 1] + current.get(j);
                else if(j == 0)             path[j] = path[j] + current.get(j);
                else                        path[j] = Math.min(path[j - 1], path[j]) + current.get(j);
            }
        }
        int min = path[0];
        for(int i = 1; i < path.length; i++){
            if(path[i] < min) min = path[i];
        }
        return min;
    }

    /**
     * Given a m x n grid filled with non-negative numbers,
     * find a path from top left to bottom right which minimizes the sum of all numbers along its path.
     */
    public int minPathSumInGrid(int[][] grid) {
        int n = grid.length;
        int m = grid[0].length;
        int[][] path = new int[n][m];
        path[0][0] = grid[0][0];
        for(int i = 1; i < n; i++) path[i][0] = path[i-1][0] + grid[i][0];
        for(int j = 1; j < m; j++) path[0][j] = path[0][j-1] + grid[0][j];
        for(int i = 1; i < n; i++){
            for(int j = 1; j < m; j++){
                path[i][j]  = Math.min(path[i-1][j], path[i][j-1]) + grid[i][j];
            }
        }
        return path[n-1][m-1];
    }

    /**
     * Given a 2D binary matrix filled with 0's and 1's,
     * find the largest square containing all ones and return its area.
     */
    public int maximalSquare(char[][] matrix){
        if(matrix.length == 0) return 0;
        int max = 0;
        int n = matrix.length;
        int m = matrix[0].length;
        int[][] maker = new int[n][m];

        for(int i = 0; i < n; i++){
            maker[i][0] = matrix[i][0] == '1'? 1 : 0;
        }

        for(int j = 1; j < n; j++){
            maker[0][j] = matrix[0][j] == '1'? 1 : 0;
        }

        for(int i = 1; i < n; i++){
            for(int j = 1; j < m; j++){
                if(matrix[i][j] == '0') maker[i][j] = 0;
                else {
                    int smaller = Math.min(maker[i - 1][j], maker[i][j - 1]);
                    maker[i][j] = Math.min(maker[i - 1][j - 1], smaller) + 1;
                    max = Math.max(max, maker[i][j]);
                }
            }
        }
        return max * max;
    }

    /**
     * Given a 2D binary matrix filled with 0's and 1's,
     * find the largest rectangle containing all ones and return its area.
     */
    //Time: O(N^2), Space: O(N^2)
    public static int maximalRectangle(char[][] matrix){
        if(matrix.length == 0) return 0;
        int n = matrix.length;
        int m = matrix[0].length;
        int[][] hisgram = new int[n][m];

        //for row 0
        for(int j = 0; j < m; j++){
            hisgram[0][j] = matrix[0][j] == '0'? 0 : 1;
        }
        //for row 1 ~ n-1
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

    //Time: O(N), Space O(N)
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

    //Time: O(N^4), Space O(N^2)
    public int maximalRectangleO4(char[][] matrix) {
        if(matrix.length == 0) return 0;
        int n = matrix.length;
        int m = matrix[0].length;
        boolean[][] maker = new boolean[n][m];

        int max = 0;
        for(int i = 0; i < n; i++){
            for(int j = 0; j < m; j++){
                if(matrix[i][j] == '0') continue;
                for(int l = i; l < n; l++){
                    for(int k = j; k < m; k++){
                        if(matrix[l][k] == '1' && ((l == i) || maker[l-1][k]) && ((k == j) || maker[l][k-1])){
                            maker[l][k] = true;
                            max = Math.max((l - i + 1) * (k - j + 1), max);
                        } else {
                            break;
                        }

                    }
                }
            }
        }
        return max;
    }
}
