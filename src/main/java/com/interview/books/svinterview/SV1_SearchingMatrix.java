package com.interview.books.svinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午12:00
 */
public class SV1_SearchingMatrix {
    public static boolean search(int[][] matrix, int target){
        if(matrix == null && matrix.length == 0) return false;
        int m = matrix.length;
        int n = matrix[0].length;

        int low = 0;
        int high = m * n;
        while(low < high){
            int mid = low + (high - low)/2;
            int row = mid / n;
            int col = mid % n;
            if(matrix[row][col] == target) return true;
            else if(matrix[row][col] < target) low = mid + 1;
            else high = mid - 1;
        }
        return false;
    }
}
