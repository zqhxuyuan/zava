package com.interview.algorithms.dp;

/**
 * Created_By: zouzhile
 * Date: 4/1/14
 * Time: 3:37 PM
 *
 * A table composed of N x M cells, each having a certain quantity of apples, is given.
 * You start from the upper-left corner. At each step you can go down or right one cell.
 * Find the maximum number of apples you can collect.
 *
 * optimal[i][j]=table[i][j] + max(optimal[i-1][j], if i>0 ; optimal[i][j-1], if j>0)
 */
public class C12_8_AppleCollection {

    public int maxNumberOfApples(int[][] table) {
        int[][] optimal = new int[table.length][table[0].length];

        // init state
        optimal[0][0] = table[0][0];
        for(int i = 0; i < table.length; i ++)
            for(int j = 0; j < table[0].length; j ++ ) {
                if(i == 0 && j == 0)
                    optimal[i][j] = table[i][j];
                else if (i == 0 && j > 0) {
                    optimal[i][j] = optimal[i][j-1] + table[i][j];
                } else if (i > 0 && j == 0) {
                    optimal[i][j] = optimal[i-1][j] + table[i][j];
                } else {
                    if(optimal[i-1][j] > optimal[i][j-1])
                        optimal[i][j] = optimal[i-1][j] + table[i][j];
                    else
                        optimal[i][j] = optimal[i][j-1] + table[i][j];
                }
            }

        return optimal[table.length - 1][table[0].length - 1];
    }
}
