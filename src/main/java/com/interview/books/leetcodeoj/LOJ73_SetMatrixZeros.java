package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午2:48
 */
public class LOJ73_SetMatrixZeros {
    //use the row and col of first zero to store the mark.
    //check if row == -1 after a full scan, directly return
    //check i != row and j != col when do reset in second stage.
    public void setZeroes(int[][] matrix) {
        if(matrix.length == 0) return;
        int row = -1;
        int col = -1;
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                if(matrix[i][j] == 0){
                    if(row == -1){
                        row = i;
                        col = j;
                    } else {
                        matrix[row][j] = 0;
                        matrix[i][col] = 0;
                    }
                }
            }
        }
        if(row == -1) return;
        for(int i = 0; i < matrix.length; i++){
            if(i != row && matrix[i][col] == 0){
                for(int j = 0; j < matrix[0].length; j++){
                    if(j != col) matrix[i][j] = 0;
                }
            }
        }
        for(int j = 0; j < matrix[0].length; j++){
            if(j != col && matrix[row][j] == 0){
                for(int i = 0; i < matrix.length; i++){
                    if(i != row) matrix[i][j] = 0;
                }
            }
        }
        for(int i = 0; i < matrix.length; i++) matrix[i][col] = 0;
        for(int j = 0; j < matrix[0].length; j++) matrix[row][j] = 0;
    }
}
