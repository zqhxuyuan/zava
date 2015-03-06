package com.interview.books.leetcodeoj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午4:19
 */
public class LOJ51_NQueens {
    //do backtracing on every position, like permutation
    //check if current position can place: two queens can't put in diagonal line：(Math.abs(queens[i] - position) == offset - i)
    //fill queens with -1 for initialize
    List<String[]> sols;
    public List<String[]> solveNQueens(int n) {
        sols = new ArrayList<>();
        int[] queens = new int[n];
        Arrays.fill(queens, -1);
        solveNQueens(n, 0, queens);
        return sols;
    }

    public void solveNQueens(int n, int offset, int[] queens){
        if(offset == n){//found a solution
            sols.add(getSolution(queens));
            return;
        }
        for(int i = 0; i < n; i++){
            if(canPlace(i, offset, queens)){
                queens[offset] = i;
                solveNQueens(n, offset + 1, queens);
            }
        }
    }

    public boolean canPlace(int position, int offset, int[] queens){
        for(int i = 0; i < offset; i++){
            if(position == queens[i] || (Math.abs(queens[i] - position) == offset - i)) return false;
        }
        return true;
    }

    public String[] getSolution(int[] queens){
        String[] sol = new String[queens.length];
        char[] chars = new char[queens.length];
        Arrays.fill(chars, '.');
        for(int i = 0; i < queens.length; i++){
            chars[queens[i]] = 'Q';
            sol[i] = String.valueOf(chars);
            chars[queens[i]] = '.';
        }
        return sol;
    }
}
