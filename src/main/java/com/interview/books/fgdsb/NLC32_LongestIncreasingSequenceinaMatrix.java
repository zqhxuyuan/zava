package com.interview.books.fgdsb;

/**
 * Created_By: stefanie
 * Date: 15-2-5
 * Time: 下午2:43
 */
public class NLC32_LongestIncreasingSequenceinaMatrix {

    public int maxLen(int[][] matrix){
        int[][] memo = new int[matrix.length][matrix[0].length];
        int maxLen = 0;
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                maxLen = Math.max(maxLen, maxLen(matrix, i, j, memo));
            }
        }
        return maxLen;
    }

    public int maxLen(int[][] matrix, int row, int col, int[][] memo){
        if(memo[row][col] != 0) return memo[row][col];
        int max = 1;
        if(row - 1 >= 0 && matrix[row - 1][col] > matrix[row][col])
            max = Math.max(max, maxLen(matrix, row - 1, col, memo) + 1);
        if(row + 1 < matrix.length && matrix[row + 1][col] > matrix[row][col])
            max = Math.max(max, maxLen(matrix, row + 1, col, memo) + 1);
        if(col - 1 >= 0 && matrix[row][col - 1] > matrix[row][col])
            max = Math.max(max, maxLen(matrix, row, col - 1, memo) + 1);
        if(col + 1 < matrix[0].length && matrix[row][col + 1] > matrix[row][col])
            max = Math.max(max, maxLen(matrix, row, col + 1, memo) + 1);
        memo[row][col] = max;
        return max;
    }

    public static void main(String[] args){
        NLC32_LongestIncreasingSequenceinaMatrix finder = new NLC32_LongestIncreasingSequenceinaMatrix();
        int[][] matrix = new int[][]{
                {1,2,3,4},
                {8,7,6,5}
        };
        System.out.println(finder.maxLen(matrix));
    }
}
