package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午2:54
 */
public class LOJ74_Search2DMatrix {
    //do binary searching in range [0, rows * cols - 1];
    //while(low <= high) do search
    //convert mid into (row, col) and check the value match. row = mid / cols, and col = min % cols.
    public boolean searchMatrix(int[][] matrix, int target) {
        if(matrix.length == 0) return false;
        int rows = matrix.length;
        int cols = matrix[0].length;
        int low = 0;
        int high = rows * cols - 1;
        while(low <= high){
            int mid = low + (high - low)/2;
            int row = mid / cols;
            int col = mid % cols;
            if(matrix[row][col] == target) return true;
            else if(target < matrix[row][col]) high = mid - 1;
            else low = mid + 1;
        }
        return false;
    }
}
