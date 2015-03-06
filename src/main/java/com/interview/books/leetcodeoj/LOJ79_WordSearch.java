package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午6:04
 */
public class LOJ79_WordSearch {
    //in dfs, if(word.length() == 1) return true;
    //in dfs, visited[row][col] = true and String suffix = word.substring(1);
    //remember to mark visited[row][col] = false when return false;
    boolean[][] visited;
    char[][] board;
    public boolean exist(char[][] board, String word) {
        this.board = board;
        this.visited = new boolean[board.length][board[0].length];
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(search(i, j, word)) return true;
            }
        }
        return false;
    }

    public boolean search(int row, int col, String word){
        if(row < 0 || row >= board.length || col < 0 || col >= board[0].length) return false;
        if(visited[row][col] || board[row][col] != word.charAt(0)) return false;
        if(word.length() == 1) return true;
        visited[row][col] = true;
        String suffix = word.substring(1);
        boolean found = search(row + 1, col, suffix) || search(row - 1, col, suffix) || search(row, col + 1, suffix)
                || search(row, col - 1, suffix);
        visited[row][col] = false;
        return found;
    }
}
