package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-11-1
 * Time: 下午6:34
 */
public class C4_13A_MaxSubMatrix {
    static class SubMatrix{
        int r1;
        int r2;
        int c1;
        int c2;
        int sum;

    }

    public static SubMatrix getMax(int[][] matrix){
        int N = matrix.length;
        int M = matrix[0].length;
        SubMatrix max = new SubMatrix();
        max.sum = Integer.MIN_VALUE;

        for(int i = 0; i < N;  i++){
            int[] columnSum = new int[M];
            for(int j = i; j < N; j++){
                for(int k = 0; k < M; k++){
                    columnSum[k] += matrix[j][k];
                }
                SubMatrix localMax = getMax(columnSum);
                if(localMax.sum > max.sum){
                    max = localMax;
                    max.r1 = i;
                    max.r2 = j;
                }
            }
        }
        return max;
    }

    public static SubMatrix getMax(int[] columnSum){
        SubMatrix max = new SubMatrix();
        max.sum = 0;
        int sum = 0;
        int largest = 0;
        int start = 0;
        for(int i = 0; i < columnSum.length; i++){
            if(columnSum[i] > columnSum[largest]) largest = i;
            sum += columnSum[i];
            if(sum < 0){
                sum = 0;
                start = i;
            }
            else if(sum > max.sum){
                max.sum = sum;
                max.c1 = start;
                max.c2 = i;
            }
        }
        if(max.sum == 0){
            max.sum = columnSum[largest];
            max.c1 = largest;
            max.c2 = largest;
        }
        return max;
    }
}
