package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午4:52
 */
public class LOJ52_NQueensII {
    //backtracing using loop
    //scan from offset 0, and queens[offset] = -1;
    //while(offset >= 0 && offset < n) do searching by queens[offset]++;
    //try next position when can't fit: while(queens[offset] < n && !canPlace(offset, queens)) queens[offset]++;
    //if(queens[offset] == n) can't find a solution, offset--; backtrace
    //else if already in last queens, mark the solution, if not find the next queens by offset++, queens[offset] = -1;
    public int totalNQueens(int n) {
        int count = 0;
        int[] queens = new int[n];
        int offset = 0;
        queens[offset] = -1;
        while(offset >= 0 && offset < n){
            queens[offset]++;
            while(queens[offset] < n && !canPlace(offset, queens)) queens[offset]++;
            if(queens[offset] == n){//doesn't find a solution
                offset--;
            } else {
                if(offset == n - 1){//found a solution
                    count++;
                } else {
                    offset++;
                    queens[offset] = -1;
                }
            }
        }
        return count;
    }

    public boolean canPlace(int offset, int[] queens){
        for(int i = 0; i < offset; i++){
            if(queens[i] == queens[offset] || (Math.abs(queens[i] - queens[offset]) == offset - i)) return false;
        }
        return true;
    }
}
