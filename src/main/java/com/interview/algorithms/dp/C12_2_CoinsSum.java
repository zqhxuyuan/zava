package com.interview.algorithms.dp;

/**
 * Created_By: zouzhile
 * Date: 3/14/14
 * Time: 4:01 PM
 *
 * Given a list of N coins, their values (V1, V2, ... , VN),
 * and the total sum S. Find the minimum number of coins the sum of which is S
 * (we can use as many coins of one type as we want),
 * or report that it's not possible to select coins in such a way that they sum up to S.
 *
 * optimal[S] = min{ optimal[S-coin[i] + 1 } for all the coin
 */

public class C12_2_CoinsSum {

    public int getMinNumberOfCoints(int S, int[] values) {
        int[] optimal = new int[S + 1];
        for(int i = 1; i < optimal.length; i ++) {
            optimal[i] = -1;
        }
        for(int s = 1; s <= S; s ++)
            for(int j = 0; j < values.length; j++) {
                if(values[j] <= s && optimal[s - values[j]] + 1 > 0 && optimal[s - values[j]] + 1 < optimal[s])
                    optimal[s] = optimal[s - values[j]] + 1;
            }

        return optimal[S];
    }

    public int[] getMinCoinsSolution(int S, int[] values) {
        int[][] solution = new int[S+1][values.length]; // for sum s, record the amount of each coin
        int[] optimal = new int[S + 1];

        for(int i = 1; i < optimal.length; i ++) {
            optimal[i] = -1 ;
        }
        for(int s = 1; s <= S; s ++)
            for(int j = 0; j < values.length; j++) {
                if(values[j] <= s && optimal[s - values[j]] + 1 > 0 && optimal[s - values[j]] + 1 < optimal[s]) {
                    optimal[s] = optimal[s - values[j]] + 1;
                    // set the selected coins to the coins solution of s, plus one more coin j
                    for(int i = 0; i < values.length; i ++) {
                        solution[s][i] = solution[s - values[j]][i];
                    }
                    solution[s][j] += 1;
                }
            }

        return solution[S];
    }

}
