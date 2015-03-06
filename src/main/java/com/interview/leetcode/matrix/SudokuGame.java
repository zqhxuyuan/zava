package com.interview.leetcode.matrix;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-11-16
 * Time: 下午7:14
 *
 * https://oj.leetcode.com/problems/sudoku-solver/
 */
public class SudokuGame {

    public static boolean solve(char[][] board) {
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++){
                if(board[i][j] != '.')  continue;
                for(int k = 1; k <= 9; k++){
                    board[i][j] = (char) (k + '0');
                    if (isValid(board, i, j) && solve(board))   return true;
                    board[i][j] = '.';
                }
                return false;
            }
        }
        return true;
    }

    private static boolean isValid(char[][] board, int row, int col){  //just valid row and col and given sub cube
        boolean[] visited = new boolean[10];
        for(int j = 0;j < 9; j++){
            if(!check(board[row][j], visited)) return false;
        }
        visited = new boolean[10];
        for(int j = 0; j < 9; j++){
            if(!check(board[j][col], visited)) return false;
        }
        visited = new boolean[10];
        row = row / 3;
        col = col / 3;
        for (int m = 0; m < 3; m++) {
            for (int n = 0; n < 3; n++){
                if(!check(board[row * 3 + m][col * 3 + n], visited)) return false;
            }
        }
        return true;
    }

    private static boolean check(char digit, boolean[] visited){
        if(digit == '.') return true;
        int ch = digit - '0';
        if(visited[ch]) return false;
        visited[ch] = true;
        return true;
    }


    public static boolean isValid(char[][] board) {
        boolean[] visited = new boolean[9];

        // row
        for(int i = 0; i<9; i++){
            Arrays.fill(visited, false);
            for(int j = 0; j<9; j++){
                if(!check(visited, board[i][j]))    return false;
            }
        }

        //col
        for(int i = 0; i<9; i++){
            Arrays.fill(visited, false);
            for(int j = 0; j<9; j++){
                if(!check(visited, board[j][i]))    return false;
            }
        }

        // sub matrix
        for(int i = 0; i<9; i+= 3){
            for(int j = 0; j<9; j+= 3){
                Arrays.fill(visited, false);
                for(int k = 0; k<9; k++){
                    if(!check(visited, board[i + k / 3][j + k % 3]))
                        return false;
                }
            }
        }
        return true;

    }

    private static boolean check(boolean[] visited, char digit){
        if(digit == '.')    return true;

        int num = digit - '0';
        if ( num < 1 || num > 9 || visited[num-1])  return false;

        visited[num-1] = true;
        return true;
    }
}
