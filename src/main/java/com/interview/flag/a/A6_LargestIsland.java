package com.interview.flag.a;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-12-4
 * Time: 下午8:40
 */
public class A6_LargestIsland {
    static class Counter {
        int count = 0;
    }
    public static int[] largest(int[][] matrix){
        int islandCount = 0;
        int maxSize = 0;
        int row = matrix.length;
        int col = matrix[0].length;
        boolean[][] visited = new boolean[row][col];
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                if(matrix[i][j] == 1 && !visited[i][j]){
                    Counter counter = new Counter();
                    dfs(matrix, i, j, counter, visited);
                    islandCount++;
                    maxSize = Math.max(maxSize, counter.count);
                }
            }
        }
        return new int[]{islandCount, maxSize};
    }

    public static void dfs(int[][] matrix, int row, int col, Counter counter, boolean[][] visited){
        if(row >= matrix.length || row < 0 || col >= matrix[0].length || col < 0
                || matrix[row][col] != 1 || visited[row][col]) return;
        counter.count++;
        visited[row][col] = true;
        dfs(matrix, row + 1, col, counter, visited);
        dfs(matrix, row - 1, col, counter, visited);
        dfs(matrix, row, col + 1, counter, visited);
        dfs(matrix, row, col - 1, counter, visited);
    }


    public static void main(String[] args){
        int[][] matrix = new int[][]{
                    {1,1,0,0},
                    {1,0,1,1},
                    {1,0,1,0},
                    {1,1,0,0}};
        ConsoleWriter.printIntArray(largest(matrix));
    }
}
