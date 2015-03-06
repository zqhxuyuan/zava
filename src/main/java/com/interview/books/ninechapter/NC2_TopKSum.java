package com.interview.books.ninechapter;

import com.interview.utils.ConsoleWriter;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 下午10:46
 */
public class NC2_TopKSum {

    NC1_TopKPairSum pairSolver = new NC1_TopKPairSum();


    public int[] topK(int[][] matrix){
        for(int i = 0; i < matrix.length; i++){
            Arrays.sort(matrix[i]);
        }

        int end = matrix.length - 1;
        while (end > 0) {
            int begin = 0;
            while (begin < end) {
                matrix[begin] = pairSolver.topK(matrix[begin], matrix[end]);
                begin++;
                end--;
            }
        }
        return matrix[0];
    }

    public static void main(String[] args){
        int[][] matrix = new int[][]{
                {1,3,4,5,6},
                {2,3,4,6,7},
                {1,2,3,5,9}
        };
        NC2_TopKSum finder = new NC2_TopKSum();
        int[] pair = finder.topK(matrix);
        ConsoleWriter.printIntArray(pair);
    }
}
