package com.interview.leetcode.matrix;

/**
 * Created_By: stefanie
 * Date: 14-11-14
 * Time: 下午10:18
 *
 * 1. rotate an n*n matrix by 90 degrees (clockwise) in place and O(1) space.  {@link #rotate(int[][])}
 * 2. create a matrix n * m, and it's element placed in spiral order.  {@link #spiralMatrix(int, int)}
 * 3. given a matrix of m x n elements (m rows, n columns), return all elements of the matrix in spiral order.  {@link #spiralPrint(int[][])}
 * 4. given a m x n matrix, if an element is 0, set its entire row and column to 0. Do it in place. {@link #setZeroes(int[][])}
 *
 * Tricks:
 *   1. define a layer, as scan loop, and matrix position is defined using layer, n and m.
 *         last = a.length - 1 - layer
 *         four angle is: matrix[layer][layer], matrix[last][layer], matrix[last][last] and matrix[layer][last]
 *      to avoid duplicate, put/get element as following in one loop
 *                  a b b b b    a matrix[layer][layer]
 *                  e       c    b matrix[layer][layer+1] ~ matrix[layer][last]
 *                  e       c    c matrix[layer+1][last]  ~ matrix[last][last]
 *                  e       c    d matrix[last][last-1]   ~ matrix[last][layer+1]
 *                  e d d d c    e matrix[last][layer]    ~ matrix[layer+1][layer]
 *      if last layer and min is odd number didn't copy d and e.
 *
 *   2. use existing space in matrix as marker
 */
public class MatrixBasicOperation {

    public static void rotate(int[][] a){
        for(int layer = 0; layer < a.length / 2; layer++){
            int last = a.length - 1 - layer;  //last element in this layer
            for(int i = 0; i < last - layer; i++){
                int tmp = a[layer][layer + i];
                a[layer][layer + i] = a[last - i][layer];
                a[last - i][layer]  = a[last][last - i];
                a[last][last - i]   = a[layer + i][last];
                a[layer + i][last]  = tmp;
            }
        }
    }

    public static int[][] spiralMatrix(int n, int m) {
        int counter = 1;
        int[][] matrix = new int[n][m];
        int min = Math.min(n, m);
        for(int layer = 0; layer < (min+1)/2; layer++){
            matrix[layer][layer] = counter++;
            for(int offset = layer + 1; offset < m - layer; offset++) matrix[layer][offset] = counter++;
            for(int offset = layer + 1; offset < n - layer; offset++) matrix[offset][m - 1 - layer] = counter++;
            if(layer == (min+1)/2 - 1 && min % 2 == 1) break;   //if last layer and min is odd number
            for(int offset = m - 2 - layer; offset > layer; offset--) matrix[n - 1 - layer][offset] = counter++;
            for(int offset = n - 1 - layer; offset > layer; offset--) matrix[offset][layer] = counter++;
        }
        return matrix;
    }

    public static int[] spiralPrint(int[][] matrix){
        int n = matrix.length;
        int m = matrix[0].length;
        int[] array = new int[n * m];
        int counter = 0;
        int min = Math.min(n, m);
        for(int layer = 0; layer < (min+1)/2; layer++){
            array[counter++] = matrix[layer][layer];
            for(int offset = layer + 1; offset < m - layer; offset++) array[counter++] = matrix[layer][offset];
            for(int offset = layer + 1; offset < n - layer; offset++) array[counter++] = matrix[offset][m - 1 - layer];
            if(layer == (min+1)/2 - 1 && min % 2 == 1) break;   //if last layer and min is odd number
            for(int offset = m - 2 - layer; offset > layer; offset--) array[counter++] = matrix[n - 1 - layer][offset];
            for(int offset = n - 1 - layer; offset > layer; offset--) array[counter++] = matrix[offset][layer];
        }
        return array;
    }

    /**
     *  Solution:
     *      1. Scan the matrix and make the rows and column need be reset.
     *      2. Reset the column. To avoid duplicate reset, first reset the rows and mark non-zero rows, every column just reset the non-zero rows.
     *
     *      Time: O(M*N)    Space: O(N+M)
     */
    public static void setZeroes(int[][] matrix) {
        boolean firstRowZero = false;
        boolean firstColZero = false;
        firstRowZero = scanRow(matrix, 0);
        firstColZero = scanCol(matrix, 0);

        for(int i = 1; i < matrix.length; i++){
            if(scanRow(matrix, i)) matrix[i][0] = 0;
        }
        for(int i = 1; i < matrix[0].length; i++){
            if(scanCol(matrix, i)) matrix[0][i] = 0;
        }

        for(int i = 1; i < matrix.length; i++){
            if(matrix[i][0] == 0) setRow(matrix, i);
        }
        for(int i = 1; i < matrix[0].length; i++){
            if(matrix[0][i] == 0) setCol(matrix, i);
        }

        if(firstRowZero) setRow(matrix, 0);
        if(firstColZero) setCol(matrix, 0);
    }

    private static boolean scanRow(int[][] matrix, int row){
        for(int i = 0; i < matrix[0].length; i++){
            if(matrix[row][i] == 0) return true;
        }
        return false;
    }

    private static void setRow(int[][] matrix, int row){
        for(int i = 0; i < matrix[0].length; i++)   matrix[row][i] = 0;
    }

    private static boolean scanCol(int[][] matrix, int col){
        for(int i = 0; i < matrix.length; i++){
            if(matrix[i][col] == 0) return true;
        }
        return false;
    }

    private static void setCol(int[][] matrix, int col){
        for(int i = 0; i < matrix.length; i++)  matrix[i][col] = 0;
    }
}
