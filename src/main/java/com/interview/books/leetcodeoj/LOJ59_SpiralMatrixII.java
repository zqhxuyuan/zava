package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午7:48
 */
public class LOJ59_SpiralMatrixII {
    //use layer, loop from [0 ~ (n + 1)/2];
    //when n != m and min(m, n) is odd, omit the bottom and left loop on last round
    public int[][] generateMatrix(int n) {
        int[][] matrix = new int[n][n];
        int counter = 1;
        for(int layer = 0; layer < (n + 1)/2; layer++){
            matrix[layer][layer] = counter++;
            for(int i = layer + 1; i < n - layer; i++)  matrix[layer][i] = counter++;
            for(int i = layer + 1; i < n - layer; i++)  matrix[i][n-layer-1] = counter++;
            //if(layer == (n + 1)/2 - 1 && n % 2 == 1) break;
            for(int i = n - layer - 2; i >= layer; i--) matrix[n-layer-1][i] = counter++;
            for(int i = n - layer - 2; i > layer; i--)  matrix[i][layer] = counter++;
        }
        return matrix;
    }
}
