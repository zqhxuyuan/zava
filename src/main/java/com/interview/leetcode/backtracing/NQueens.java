package com.interview.leetcode.backtracing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The n-queens puzzle is the problem of placing n queens on an n×n chessboard such that no two queens attack each other.
 * Given an integer n, return all distinct solutions to the n-queens puzzle.
 */
public class NQueens {


    public List<String[]> solutions(int n) {
        List<String[]> sols = new ArrayList<>();
        int[] queens = new int[n];
        int offset = 0;
        queens[offset] = -1;
        while (offset >= 0 && offset < n) {
            queens[offset]++;
            while (queens[offset] < n && canPlace(offset, queens) == false) queens[offset]++;
            if (queens[offset] == n) {
                offset--;
            } else {
                if (offset == n - 1) sols.add(getSolution(queens));
                else {
                    offset++;
                    queens[offset] = -1;
                }
            }
        }
        return sols;
    }

    public int solutionCount(int n) {
        int count = 0;

        int[] queens = new int[n];
        int offset = 0;
        queens[offset] = -1;

        while (offset >= 0 && offset < n) {
            queens[offset]++;
            while (queens[offset] < n && !canPlace(offset, queens)) queens[offset]++;
            if (queens[offset] == n) {
                offset--;
            } else {
                if (offset == n - 1) count++;
                else {
                    offset++;
                    queens[offset] = -1;
                }
            }
        }
        return count;
    }


    public boolean canPlace(int offset, int[] queens) {
        for (int i = 0; i < offset; i++) {
            if (queens[i] == queens[offset] || (Math.abs(queens[i] - queens[offset]) == offset - i)) return false;
        }
        return true;
    }

    public String[] getSolution(int[] queens) {
        String[] rows = new String[queens.length];
        char[] chars = new char[queens.length];
        Arrays.fill(chars, '.');
        for (int i = 0; i < queens.length; i++) {
            chars[queens[i]] = 'Q';
            rows[i] = String.valueOf(chars);
            chars[queens[i]] = '.';
        }
        return rows;
    }

    public static void main(String[] args) {
        NQueens solver = new NQueens();
        solver.solutionCount(1);
    }
}
