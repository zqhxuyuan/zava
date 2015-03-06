package com.interview.leetcode.backtracing;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-11-24
 * Time: 下午4:50
 */
public class SudokuGame {
    char[][] board;
    public void solveSudoku(char[][] board){
        this.board = board;
        solve();
    }

    public boolean solve(){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(board[i][j] != '.') continue;
                for(char k = '1'; k <= '9'; k++){
                    if(!valid(i, j, k)) continue;
                    board[i][j] = k;
                    if(solve()) return true;
                    else continue;
                }
                board[i][j] = '.';
                return false;
            }
        }
        return true;
    }

    public boolean valid(int x, int y, char k){
        for(int i = 0; i < 9; i++){
            if(board[x][i] == k) return false;
            if(board[i][y] == k) return false;
        }
        int row = x / 3;
        int col = y / 3;
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(board[row * 3 + i][col * 3 + j] == k) return false;
            }
        }
        return true;
    }

    public static void main(String[] args){
        String[] data = new String[]{"..9748...","7........",".2.1.9...","..7...24.",".64.1.59.",".98...3..","...8.3.2.","........6","...2759.."};
        char[][] board = new char[9][9];
        for(int i = 0; i < 9; i++){
            board[i] = data[i].toCharArray();
        }

        SudokuGame game = new SudokuGame();
        game.solveSudoku(board);
        ConsoleWriter.printIntArray(board);
    }
}
