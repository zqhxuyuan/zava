package com.interview.books.leetcodeoj;

import com.interview.utils.ConsoleWriter;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 14-12-27
 * Time: 上午10:14
 */
public class LOJ130_SurroundedRegion {
    //based on DFS may get a stackoverflow if the board is too large. better to use BFS
    //scan row 0 and rows - 1, col 0 and cols - 1 to enqueue 'O', then do BFS based on queue.
    //set board[row][col] = 'C' when enqueue, and set 'C' to 'O' and 'O' to 'X' at the end scan.
    //use row * cols + col as position identifier in queue.
    char[][] board;
    int rows;
    int cols;
    Queue<Integer> queue;
    public void solve(char[][] board) {
        if(board.length == 0) return;
        this.board = board;
        rows = board.length;
        cols = board[0].length;
        if(rows == 1 || cols == 1) return;

        queue = new LinkedList();
        for(int i = 0; i < rows; i++){
            enqueue(i, 0);
            enqueue(i, cols - 1);
        }
        for(int j = 0; j < cols; j++){
            enqueue(0, j);
            enqueue(rows - 1, j);
        }

        while(!queue.isEmpty()){
            Integer pos = queue.poll();
            int row = pos / cols;
            int col = pos % cols;
            enqueue(row + 1, col);
            enqueue(row - 1, col);
            enqueue(row, col + 1);
            enqueue(row, col - 1);
        }

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(board[i][j] == 'C') board[i][j] = 'O';
                else if(board[i][j] == 'O') board[i][j] = 'X';
            }
        }
    }

    public void enqueue(int row, int col){
        if(row >= 0 && row < rows && col >= 0 && col < cols && board[row][col] == 'O'){
            board[row][col] = 'C';
            queue.offer(row * cols + col);
        }
    }

    public static void main(String[] args){
        char[][] board = new char[][]{
                "XXXX".toCharArray(),
                "XOOX".toCharArray(),
                "XXOX".toCharArray(),
                "XOXX".toCharArray()
        };
        LOJ130_SurroundedRegion finder = new LOJ130_SurroundedRegion();
        finder.solve(board);
        ConsoleWriter.printIntArray(board);
    }
}
