package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-11-9
 * Time: 上午12:03
 */
public class C4_1A_SpiralMatrixGenerator {

    public static int[][] generateMatrix(int n) {
        int counter = 1;
        int[][] matrix = new int[n][n];
        for(int layer = 0; layer < (n+1)/2; layer++){
            matrix[layer][layer] = counter++;
            for(int offset = layer + 1; offset < n - layer; offset++) matrix[layer][offset] = counter++;
            for(int offset = layer + 1; offset < n - layer; offset++) matrix[offset][n - 1 - layer] = counter++;
            for(int offset = n - 2 - layer; offset > layer; offset--) matrix[n - 1 - layer][offset] = counter++;
            for(int offset = n - 1 - layer; offset > layer; offset--) matrix[offset][layer] = counter++;
        }
        return matrix;
    }
}
