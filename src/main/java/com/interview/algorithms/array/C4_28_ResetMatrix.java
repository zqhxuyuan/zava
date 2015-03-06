package com.interview.algorithms.array;

import java.util.HashSet;
import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 14-7-6
 * Time: 下午9:41
 *
 * Write a algorithm such that if an element in an element in a M*N matrix is 0, it's entire row and column is set to 0.
 *
 * Solution:
 *      1. Scan the matrix and make the rows and column need be reset.
 *      2. Reset the column. To avoid duplicate reset, first reset the rows and mark non-zero rows, every column just reset the non-zero rows.
 *
 *      Time: O(M*N)    Space: O(N+M)
 */
public class C4_28_ResetMatrix {

    public static int[][] reset(int[][] matrix){
        boolean[] rows = new boolean[matrix.length];
        boolean[] columns = new boolean[matrix[0].length];
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                if(matrix[i][j] == 0){
                    rows[i] = true;
                    columns[j] = true;
                }
            }
        }
        Set<Integer> nonZeroRows = new HashSet<Integer>();
        for(int i = 0; i < rows.length; i++){
            if(rows[i]){
                for(int m = 0; m < matrix[i].length; m++) matrix[i][m] = 0;
            } else {
                nonZeroRows.add(i);
            }
        }
        for(Integer n : nonZeroRows){
            for(int j = 0; j < columns.length; j++){
                if(columns[j])  matrix[n][j] = 0;
            }
        }
        return matrix;
    }

    public static int[][] resetOptimized(int[][] matrix){
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
        return matrix;
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
