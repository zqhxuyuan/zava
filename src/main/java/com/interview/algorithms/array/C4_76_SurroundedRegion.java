package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-11-9
 * Time: 下午5:12
 */
public class C4_76_SurroundedRegion {

    public static void flip(char[][] board) {
        if(board.length < 3 || board[0].length < 3) return;
        int rows = board.length;
        int cols = board[0].length;
        for(int i = 1; i < rows - 1; i++){
            for(int j = 1; j < cols - 1; j++){
                if(board[i][j] == 'O') board[i][j] = '$';
            }
        }

        for(int j = 1; j < cols; j++){
            if(board[0][j] == 'O') explore(board, 1, j);
            if(board[rows - 1][j] == 'O') explore(board, rows - 2, j);
        }

        for(int i = 1; i < rows; i++){
            if(board[i][0] == 'O') explore(board, i, 1);
            if(board[i][cols - 1] == 'O') explore(board, i, cols - 2);
        }

        for(int i = 1; i < rows; i++){
            for(int j = 1; j < cols; j++){
                if(board[i][j] == '$') board[i][j] = 'X';
            }
        }
    }

    private static void explore(char[][] board, int row, int col){
        if(board[row][col] == '$'){
            board[row][col] = 'O';
            if(row - 1 > 0) explore(board, row - 1, col);
            if(row + 1 < board.length - 1) explore(board, row + 1, col);
            if(col - 1 > 0) explore(board, row, col - 1);
            if(col + 1 < board.length - 1) explore(board, row, col + 1);
        }
    }
}
