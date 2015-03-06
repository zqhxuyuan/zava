package com.interview.leetcode.backtracing;

/**
 * Created_By: stefanie
 * Date: 14-11-24
 * Time: 下午4:29
 *
 * https://oj.leetcode.com/problems/word-search/
 */
public class WordSearch {
    char[][] board;
    boolean[][] visited;
    public boolean exist(char[][] board, String word) {
        this.board = board;
        this.visited = new boolean[board.length][board[0].length];
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(exist(word, i, j)) return true;
            }
        }
        return false;
    }

    public boolean exist(String word, int x, int y){
        if(x < 0 || x >= board.length || y < 0 || y >= board[0].length) return false;
        if(visited[x][y] || board[x][y] != word.charAt(0)) return false;
        if(word.length() == 1) return true;
        else {
            visited[x][y] = true;
            String next = word.substring(1);
            if(exist(next, x + 1, y) || exist(next, x - 1, y) || exist(next, x, y + 1) || exist(next, x, y - 1)) return true;
            visited[x][y] = false;
            return false;
        }
    }
}
