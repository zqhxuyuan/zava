package com.interview.leetcode.matrix;

/**
 * Created_By: zouzhile
 * Date: 11/15/14
 * Time: 3:16 PM
 */
public class UniqPaths {

    /**
        Basic Unique Paths https://oj.leetcode.com/problems/unique-paths/
     */
    public int uniquePaths(int m, int n) {
        int[][] paths = new int[m][n];

        for(int i = 0; i < m; i ++) paths[i][0] = 1;
        for(int j = 0; j < n; j ++) paths[0][j] = 1;

        for(int row = 1; row < m; row ++)
            for(int col = 1; col < n; col ++)
                paths[row][col] = paths[row-1][col] + paths[row][col-1];

        return paths[m-1][n-1];
    }

    /**
     * paths with obstacles: https://oj.leetcode.com/problems/unique-paths-ii/
     */

    public int uniquePathsWithObstacles(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int[][] paths = new int[m][n];
        for(int row = 0; row < m; row ++)
            if(grid[row][0] == 1) {
                for(int j = row; j < m; j ++) paths[j][0] = 0;
                break;
            } else
                paths[row][0] = 1;

        for(int col = 0; col < n; col ++)
            if(grid[0][col] == 1) {
                for(int j = col; j < n; j ++) paths[0][j] = 0;
                break;
            } else
                paths[0][col] = 1;

        for(int row = 1; row < m; row ++) {
            for(int col = 1; col < n; col ++) {
                if(grid[row][col] == 1) paths[row][col] = 0;
                else {
                    if(grid[row-1][col] != 1) paths[row][col] += paths[row-1][col];
                    if(grid[row][col - 1] != 1) paths[row][col] += paths[row][col-1];
                }
            }
        }

        return paths[m-1][n-1];
    }
}
