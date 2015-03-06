package com.interview.algorithms.array;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/11/14
 * Time: 3:30 PM
 */
public class C4_13_MaxSubMatrix {
    public static int find(int[][] matrix){
        int[][] total = matrix;
        for (int i = 1; i < matrix[0].length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                total[i][j] += total[i-1][j];
            }
        }

        int maximum = Integer.MIN_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i; j < matrix.length; j++) {
                //result 保存的是从 i 行 到第 j 行 所对应的矩阵上下值的和
                int[] result = new int[matrix[0].length];
                for (int f = 0; f < matrix[0].length; f++) {
                    if (i == 0) {
                        result[f] = total[j][f];
                    } else {
                        result[f] = total[j][f] - total[i - 1][f];
                    }
                }
                int maximal = C4_29_MaxSubArraySum.max(result);

                if (maximal > maximum) {
                    maximum = maximal;
                }
            }
        }

        return maximum;
    }
}
