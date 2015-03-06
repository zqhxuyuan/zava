package com.interview.leetcode.matrix;

/**
 * Created_By: stefanie
 * Date: 14-11-16
 * Time: 下午3:12
 * <p/>
 * Given a 2D board and a word, find if the word exists in the grid.
 * The word can be constructed from letters of sequentially adjacent cell, where "adjacent" cells are those horizontally or
 * vertically neighboring. The same letter cell may not be used more than once.
 * For example, Given board =
 *  [
 *      ["ABCE"],
 *      ["SFCS"],
 *      ["ADEE"]
 *  ]
 *      word = "ABCCED", -> returns true,
 *      word = "SEE", -> returns true,
 *      word = "ABCB", -> returns false.
 *
 * Solution:
 *  1. find the start point which board[i][j] == first char
 *  2. then do a dfs on the board, using visited to tracking if the node is already visited.
 *      if found the word, return true
 *      else make the visited[i][j] = false, and move to next try
 *
 * Tricks:
 *  1. DFS on graph, using boolean[][] visited to tracking
 *  2. clear definition of non-solution: index out of range, char not equals.
 */
public class WordSearch {
    static char[][] board;
    static boolean[][] visited;

    public static boolean exist(char[][] board, String word) {
        if (board == null || board.length * board[0].length < word.length()) return false;
        board = board;
        visited = new boolean[board.length][board[0].length];
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[0].length; j++) {
                if (exist(word, i, j, 0)) return true;
            }
        return false;
    }

    private static boolean exist(String word, int x, int y, int offset) {
        if (x < 0 || x >= board.length || y < 0 || y >= board[0].length) return false;

        if (board[x][y] != word.charAt(offset) || visited[x][y]) return false;

        visited[x][y] = true;
        if (offset == word.length() - 1) return true; // the last char equals
        boolean found = exist(word, x - 1, y, offset + 1) ||
                exist(word, x + 1, y, offset + 1) ||
                exist(word, x, y - 1, offset + 1) ||
                exist(word, x, y + 1, offset + 1);
        if (!found) visited[x][y] = false;
        return found;
    }
}
