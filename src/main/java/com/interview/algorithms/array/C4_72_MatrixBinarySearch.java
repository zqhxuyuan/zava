package com.interview.algorithms.array;

/**
 * Created_By: zouzhile
 * Date: 11/1/14
 * Time: 5:11 PM
 */
public class C4_72_MatrixBinarySearch {

    public boolean search(int[][] A, int N) {
        return search(A, 0, 0, A.length - 1, A[0].length - 1, 6);
    }

    public boolean search(int[][] A, int x1, int y1, int x2, int y2, int N) {
        /*
            1   2   3   4
            5   6   7   8
            9   10  11  12
            13  14  15  16
            17  18  19  20
        */
        if(x1 > x2 || y1 > y2)
            return false;
        int centerX = (x1 + x2) / 2;
        int centerY = (y1 + y2) / 2;
        int center = A[centerX][centerY];

        if(N == center)
            return true;
        else if(N < center)
            return  search(A, centerX + 1, y1, x2, centerY, N) ||
                    search(A, x1, centerY+1, centerX, y2, N) ||
                    search(A, x1, y1, centerX, centerY, N);
        else
            return search(A, centerX + 1, y1, x2, centerY, N) ||
                   search(A, x1, centerY+1, centerX, y2, N) ||
                   search(A, centerX+1, centerY + 1, x2, y2, N);
    }

    public boolean searchOptimized(int[][] A, int N) {
        int row = 0, col = A[0].length - 1;
        int val;
        while(row < A.length && col >=0) {
            val = A[row][col];
            if(val == N)
                return true;
            else if(N < val)
                col --;
            else
                row ++;
        }
        return false;
    }

    public static void main(String[] args) {
        int[][] array = {{1,2,3,4}, {2,3,4,5},{3,4,5,6},{4,5,6,7}};
        C4_72_MatrixBinarySearch searcher = new C4_72_MatrixBinarySearch();
        System.out.println(searcher.search(array, 6));
        System.out.println(searcher.searchOptimized(array, 9));
    }
}
