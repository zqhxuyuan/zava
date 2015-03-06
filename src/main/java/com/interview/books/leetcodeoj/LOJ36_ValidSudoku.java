package com.interview.books.leetcodeoj;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-12-19
 * Time: 下午9:23
 */
public class LOJ36_ValidSudoku {
    //check each row, col, and cube
    //be careful about the index, row/col/i/j
    public boolean isValidSudoku(char[][] board) {
        boolean[] mark = new boolean[10];
        for(int row = 0; row < board.length; row++){
            if(!validRow(board, row, mark)) return false;
        }
        for(int col = 0; col < board[0].length; col++){
            if(!validCol(board, col, mark)) return false;
        }
        for(int row = 0; row < board.length; row += 3){
            for(int col = 0; col < board[0].length; col += 3){
                if(!validCube(board, row, col, mark)) return false;
            }
        }
        return true;
    }

    public boolean validRow(char[][] board, int row, boolean[] mark){
        Arrays.fill(mark, false);
        for(int j = 0; j < board[0].length; j++){
            char ch = board[row][j];
            if(ch == '.') continue;
            else if(mark[ch -'0']) return false;
            else mark[ch - '0'] = true;
        }
        return true;
    }

    public boolean validCol(char[][] board, int col, boolean[] mark){
        Arrays.fill(mark, false);
        for(int i = 0; i < board.length; i++){
            char ch = board[i][col];
            if(ch == '.') continue;
            else if(mark[ch -'0']) return false;
            else mark[ch - '0'] = true;
        }
        return true;
    }

    public boolean validCube(char[][] board, int row, int col, boolean[] mark){
        Arrays.fill(mark, false);
        for(int i = row; i < row + 3; i++){
            for(int j = col; j < col + 3; j++){
                char ch = board[i][j];
                if(ch == '.') continue;
                else if(mark[ch -'0']) return false;
                else mark[ch - '0'] = true;
            }
        }
        return true;
    }

    public static void main(String[] args){
        LOJ36_ValidSudoku validator = new LOJ36_ValidSudoku();
        String[] strs = new String[] {".87654321","2........","3........","4........","5........","6........","7........","8........","9........"};
        char[][] board = new char[9][9];
        for(int i = 0; i < strs.length; i++){
            board[i] = strs[i].toCharArray();
        }
        System.out.println(validator.isValidSudoku(board));
    }
}
