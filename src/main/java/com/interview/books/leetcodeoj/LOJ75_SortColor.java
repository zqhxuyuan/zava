package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午3:07
 */
public class LOJ75_SortColor {
    //keep two pointer: small(before element are smaller than key), equal(between small and equal are equals to key)
    //scan the array
    //if A[j] == key, swap(A, ++equals, j)
    //if A[j] < key, swap(A, ++small, j) and equal++, then check if(A[equal] > A[j]) swap(A, equal, j);
    public void sortColors(int[] A) {
        int key = 1;
        int small = -1;
        int equal = -1;
        for(int j = 0; j < A.length; j++){
            if(A[j] < key){
                swap(A, ++small, j);
                equal++;
                if(A[equal] > A[j]) swap(A, equal, j);
            } else if(A[j] == key){
                swap(A, ++equal, j);
            }
        }
    }

    public void swap(int[] A, int i, int j){
        int temp = A[i];
        A[i] = A[j];
        A[j] = temp;
    }
}
