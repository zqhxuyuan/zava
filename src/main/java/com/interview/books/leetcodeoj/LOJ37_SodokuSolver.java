package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-21
 * Time: 下午3:58
 */
public class LOJ37_SodokuSolver {
    //backtracing: find a placable char ['0' - '9'], if find, call solve() for next position, if can find, return false.
    //char k is between '1' to '9'
    //do loop on each position (i,j) to find a position is '.'
    char[][] board;
    public void solveSudoku(char[][] board){
        this.board = board;
        solve();
    }

    public boolean solve(){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(board[i][j] != '.') continue;
                for(char k = '1'; k <= '9'; k++){
                    if(verify(i, j, k)){
                        board[i][j] = k;
                        if(solve()) return true;
                        board[i][j] = '.';
                    }
                }
                if(board[i][j] == '.') return false;
            }
        }
        return true;
    }

    public boolean verify(int row, int col, char value){
        for(int i = 0; i < board.length; i++){
            if(board[row][i] == value) return false;
            if(board[i][col] == value) return false;
        }
        int rowStart = (row / 3) * 3;
        int colStart = (col / 3) * 3;
        for(int i = rowStart; i < rowStart + 3; i++){
            for(int j = colStart; j < colStart + 3; j++){
                if(board[i][j] == value) return false;
            }
        }
        return true;
    }
}
