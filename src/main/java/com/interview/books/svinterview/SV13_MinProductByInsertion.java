package com.interview.books.svinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午3:16
 */
public class SV13_MinProductByInsertion {
    /**
     * State: product[i][j] is the min product of A[0]...A[i-1] * B[0]...B[j-1]
     * Initialize:
     *      product[0][0] = A[0] * B[0]
     *      product[0][j] = min(A[0] * B[j], product[0][j-1])
     * Function:
     *      when i == j;                 product[i][j] = A[i] * B[j] + product[i-1][j-1];
     *          when i == j can't insert 0
     *      when 0 < i < j <= i + n - m; product[i][j] = min(A[i] * B[j] + product[i-1][j-1], product[i][j-1])
     * Result: product[m-1][n-1]
     */
    public int minProduct(int[] A, int[] B){
        int m = A.length;
        int n = B.length;
        int[][] product = new int[m][n];
        //init
        product[0][0] = A[0] * B[0];
        for(int j = 1; j < n; j++) product[0][j] = Math.min(A[0] * B[j], product[0][j - 1]);

        //function
        for(int i = 1; i < m; i++){
            for(int j = i; j <= i + (n - m); j++){
                if(j == i){   //can insert 0
                    product[i][j] = product[i - 1][j - 1] + A[i] * B[j];
                } else {      //product[i][j - 1] is insert 0 in A[i]
                    product[i][j] = Math.min(product[i - 1][j - 1] + A[i] * B[j], product[i][j - 1]);
                }
            }
        }
        //result
        return product[m - 1][n - 1];
    }

    public static void main(String[] args){
        SV13_MinProductByInsertion finder = new SV13_MinProductByInsertion();
        int[] A = new int[]{1, 2, 4};
        int[] B = new int[]{4, 1, 2, 3};
        System.out.println(finder.minProduct(A, B));  //14
    }
}
