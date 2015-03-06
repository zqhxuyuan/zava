package com.interview.leetcode.graph;

import com.interview.utils.ConsoleWriter;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created_By: stefanie
 * Date: 14-11-23
 * Time: 下午3:27
 */
public class SurroundingRegion {

    static class DFSSolution{
        public void solve(char[][] board) {
            if(board == null || board.length <= 1 || board[0].length <= 1) return;
            int rows = board.length;
            int cols = board[0].length;

            for(int i = 1; i < rows - 1; i++){
                for(int j = 1; j < cols - 1; j++){
                    if(board[i][j] == 'O') board[i][j] = '?';
                }
            }

            for(int j = 1; j < cols - 1; j++){
                if(board[0][j] == 'O') connect(board, 1, j);
                if(board[rows - 1][j] == 'O') connect(board, rows - 2, j);
            }
            for(int i = 1; i < rows - 1; i++){
                if(board[i][0] == 'O') connect(board, i, 1);
                if(board[i][cols - 1] == 'O') connect(board, i, cols - 2);
            }

            for(int i = 1; i < rows - 1; i++){
                for(int j = 1; j < cols - 1; j++){
                    if(board[i][j] == '?') board[i][j] = 'X';
                }
            }
        }

        public void connect(char[][] board, int row, int col){
            if(board[row][col] != '?') return;
            board[row][col] = 'O';
            if(row - 1 > 0) connect(board, row - 1, col);
            if(row + 1 < board.length - 1) connect(board, row + 1, col);
            if(col - 1 > 0) connect(board, row, col - 1);
            if(col + 1 < board[0].length - 1) connect(board, row, col + 1);
        }
    }

    static class BSFSolution{
        char[][] board;
        Queue<Integer> queue;
        int rows;
        int cols;

        public void solve(char[][] board) {
            if(board.length == 0 || board[0].length == 0) return;
            this.board = board;
            queue = new LinkedList<>();
            rows = board.length;
            cols = board[0].length;

            for(int i = 0; i < rows; i++){
                enqueue(i, 0);
                enqueue(i, cols - 1);
            }

            for(int j = 0; j < cols; j++){
                enqueue(0, j);
                enqueue(rows - 1, j);
            }

            while(queue.size() > 0){
                Integer pos = queue.poll();
                int row = pos / cols;
                int col = pos % cols;

                board[row][col] = 'D';

                enqueue(row + 1, col);
                enqueue(row - 1, col);
                enqueue(row, col + 1);
                enqueue(row, col - 1);
            }

            for(int i = 0; i < rows; i++){
                for(int j = 0; j < cols; j++){
                    if(board[i][j] == 'D') board[i][j] = 'O';
                    else if(board[i][j] == 'O') board[i][j] = 'X';
                }
            }
        }

        public void enqueue(int row, int col){
            if(row >= 0 && row < rows && col >= 0 && col < cols && board[row][col] == 'O'){
                queue.offer(row * cols + col);
            }
        }
    }

    public static void main(String[] args){
        char[][] board = new char[][] {
                "XXX".toCharArray(),
                "XOX".toCharArray(),
                "XXX".toCharArray(),
        };
        DFSSolution flipper = new DFSSolution();
        flipper.solve(board);
        ConsoleWriter.printIntArray(board);
    }

}
