package com.interview.algorithms.backtracing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-7
 * Time: 上午10:56
 */
public class C17_1_NQueues {

    public List<String[]> solveNQueens(int n) {
        List<String[]> sols = new ArrayList<>();
        if(n <= 0) return sols;

        int[] queens = new int[n];
        int offset = 0;

        queens[0] = -1;
        while (offset >= 0 && offset < n) {
            queens[offset] = queens[offset] + 1;
            while (queens[offset] < n && place(offset, queens) == false) {
                queens[offset] = queens[offset] + 1;
            }
            if (queens[offset] < n) {
                if (offset == n - 1) {
                   sols.add(printSolution(queens, n));
                } else {
                    offset = offset + 1;
                    queens[offset] = -1;
                }
            }
            else offset = offset - 1;
        }
        return sols;
    }


    private boolean place(int current, int[] queens) {
        int i = 0;
        while (i < current) {
            if (queens[i] == queens[current] || Math.abs(queens[i] - queens[current]) == Math.abs(i - current)) return false;
            i = i + 1;
        }
        return true;
    }


    private String[] printSolution(int[] queens, int n){
        String[] sol = new String[n];
        char[] chars = new char[n];
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(j != queens[i]) chars[j] = '.';
                else chars[j] = 'Q';
            }
            sol[i] = String.valueOf(chars);
            chars = new char[n];
        }
        return sol;
    }


}
