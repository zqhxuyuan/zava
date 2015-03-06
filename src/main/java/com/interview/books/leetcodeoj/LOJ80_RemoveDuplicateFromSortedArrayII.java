package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午8:44
 */
public class LOJ80_RemoveDuplicateFromSortedArrayII {
    //tracking occurrence, if A[i] == A[i-1] && occurrence == 2, just continue
    //if A[i] != A[i-1] then occurrence = 1; else occurrence++, and copy A[i] to A[offset++];
    public int removeDuplicates(int[] A) {
        if(A.length == 0) return 0;
        int offset = 1;
        int occurrence = 1;
        for(int i = 1; i < A.length; i++){
            if(A[i] == A[i-1] && occurrence == 2) continue;
            if(A[i] != A[i-1]) occurrence = 1;
            else occurrence++;
            A[offset++] = A[i];
        }
        return offset;
    }
}
