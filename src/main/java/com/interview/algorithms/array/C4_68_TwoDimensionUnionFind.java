package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-10-19
 * Time: 下午5:08
 */
public class C4_68_TwoDimensionUnionFind {
    public static int[][] unionfind(int[][] matrix){
        int[][] flag = new int[matrix.length][matrix[0].length];
        int counter = 1;
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                if(matrix[i][j] == 1){
                    if(i > 0 && matrix[i-1][j] == 1) {
                        flag[i][j] = flag[i-1][j];
                        update(matrix, flag, i, j);
                        continue;
                    }
                    if(j > 0 && matrix[i][j-1] == 1) {
                        flag[i][j] = flag[i][j-1];
                        update(matrix, flag, i, j);
                        continue;
                    }
                    flag[i][j] = counter++;
                }
            }
        }
        return flag;
    }

    private static void update(int[][] matrix, int[][] flag, int i, int j){
        if(i > 0 && matrix[i - 1][j] == 1 && flag[i - 1][j] != flag[i][j]){
            flag[i-1][j] = flag[i][j];
            update(matrix, flag, i - 1, j);
        }
        if(j > 0 && matrix[i][j - 1] == 1 && flag[i][j - 1] != flag[i][j]){
            flag[i][j - 1] = flag[i][j];
            update(matrix, flag, i, j - 1);
        }
    }
}
