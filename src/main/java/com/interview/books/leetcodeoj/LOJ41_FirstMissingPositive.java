package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-21
 * Time: 下午5:18
 */
public class LOJ41_FirstMissingPositive {
    //put element in position it should be, 1 to A[0], 2 to A[1], then find the first element doesn't exist.
    //1. during scan, if swap, need i--;
    //2. position != i && A[position] != A[i], then swap
    public int firstMissingPositive(int[] A) {
        for(int i = 0; i < A.length; i++){
            if(A[i] <= 0 || A[i] > A.length) continue;
            int position = A[i] - 1;
            if(position != i && A[position] != A[i]){
                swap(A, i, position);
                i--;
            }
        }
        for(int i = 0; i < A.length; i++){
            if(A[i] != i + 1) return i + 1;
        }
        return A.length + 1;
    }

    private void swap(int[] A, int i, int j){
        int temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }
}
