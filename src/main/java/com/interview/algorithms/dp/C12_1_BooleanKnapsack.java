package com.interview.algorithms.dp;

/**
 * Created_By: zouzhile
 * Date: 2/23/14
 * Time: 5:02 PM
 *
 * Provide three different solutions for Boolean Knapsack
 *  1. Recursive O(2^n)
 *  2. DP Solution 1:  define opt[n][w] = max profit of packing items 1..n with weight limit w
 *      scan the items, and only consider ith item  put in or not.
 *      opt[i, j] = max( opt[i-1, j-Wi] + Vi (j >= Wi), opt[i-1, j] )
 *  3. DP Solution 2:  define optimal[S], the max profit could get when put S weight in bag
 *      scan the W, try to build opt[W] and solution[W][_]
 *      opt[S] = max(opt[S-Wi]+Vi. opt[S-1]) and filter the item if it already in the bag
 */
public class C12_1_BooleanKnapsack {

    public static int getMaxValueByRecursion(int index, int W, int[] weights, int[] values) {
        if(index < 0)
            return 0;
        if(weights[index] > W) {
            return getMaxValueByRecursion(index - 1, W, weights, values);
        } else {
            return Math.max(getMaxValueByRecursion(index - 1, W, weights, values),
                    getMaxValueByRecursion(index - 1, W - weights[index], weights, values) + values[index]);
        }
    }

    public static boolean[] getMaxValueByDPS2(int W, int[] weights, int[] values) {
        int N = values.length;
        // optimal[S], the max profit could get when put S weight in bag
        // solution[S][], the solution of the max profit of S
        int[] optimal = new int[W + 1];
        boolean[][] solution = new boolean[W + 1][N];

        for(int s = 1; s <= W; s++){
            for(int j = 0; j < N; j++){
                int left = s - weights[j];
                //if jth item could put in, and optimal is larger and jth item haven't been put in before
                if(left >= 0 && optimal[left] + values[j] > optimal[s] && !solution[left][j]) {
                    optimal[s] = optimal[s - weights[j]] + values[j];
                    copySolution(solution, N, left, s);
                    solution[s][j] = true;
                }
            }
            if(s < W) {   //if have next S
                optimal[s+1] = optimal[s];
                copySolution(solution, N, s, s+1);
            }

        }
        return solution[W];
    }

    private static void copySolution(boolean[][] solution, int N, int from, int to){
        for(int i = 0; i < N; i ++) {
            solution[to][i] = solution[from][i];
        }
    }


    //NOT A GOOD SOLUTION AS getMaxValueByDPS2
    public static int getMaxValueByDPS1(int W, int[] weights, int[] values) {
        int N = weights.length; // the number of item types

        weights = shift(weights);
        values = shift(values);

        // opt[n][w] = max profit of packing items 1..n with weight limit w
        // sol[n][w] = does opt solution to pack items 1..n with weight limit w include item n?
        int[][] opt = new int[N+1][W+1];
        boolean[][] sol = new boolean[N+1][W+1];

        for (int n = 1; n <= N; n++) {
            for (int w = 1; w <= W; w++) {

                // don't take item n
                int option1 = opt[n-1][w];

                // take item n
                int option2 = Integer.MIN_VALUE;
                if (weights[n] <= w)
                    option2 = values[n] + opt[n-1][w-weights[n]];

                // select better of two options
                opt[n][w] = Math.max(option1, option2);
                sol[n][w] = (option2 > option1);
            }
        }

        return opt[N][W];
    }

    private static int[] shift(int[] array){
        int[] result = new int[array.length + 1];
        for(int i = 0; i < array.length; i++)
            result[i+1] = array[i];
        result[0] = 0;
        return  result;
    }

}
