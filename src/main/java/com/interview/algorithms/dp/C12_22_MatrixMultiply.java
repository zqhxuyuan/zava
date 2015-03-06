package com.interview.algorithms.dp;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/19/14
 * Time: 1:40 PM
 *
 * [Introduction to Algorithm Chp.15] The matrix multiply
 * There is M matrix, A1 A2 .. AM, write code to find the smallest cost ways to make these M matrix could multiply.
 * (A1 * (A2 * A3)) or ((A1 * A2) * A3) give the same answer, but may cause different of computing effect when A1 is a very small matrix
 * And A1*A1 could multiply when dimensionality is the same, so d[N] save the dimensions, Ai's dimension is di-1 and di
 *
 * define opt[i][j] as the min cost of matrix i multiply to matrix j
 *      opt[i][j] = min{ opt[i][k] + opt[k][j] + d[i-1] * d[k] * d[j] } for all the k in [i,j-1]
 *
 *
 * Since we need opt[i][k] calculated before opt[i][j], so the loop can't be for each i, each j, it should have length
 *      for length from 2 - N-1
 *          for i from 1 to N - l //the start
 *              j = i + l - 1; //the end
 *              for k from i to j find the min opt[i][j] and mark the split in sol[i][j]
 */
public class C12_22_MatrixMultiply {
    static class Result {
        int cost;
        String combination;
    }

    public static Result count(int[] dim) {
        int N = dim.length;
        int[][] opt = new int[N][N];
        int[][] sol = new int[N][N];

        for (int i = 1; i < N; i++) opt[i][i] = 0;

        for (int l = 2; l < N; l++) {  // l is the length of matrix chain
            for (int i = 1; i < N - l + 1; i++) { // i is the start of the chain
                int j = i + l - 1; // j is the end of the chain
                opt[i][j] = Integer.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    int ten = opt[i][k] + opt[k + 1][j] + dim[i - 1] * dim[k] * dim[j];
                    if (ten < opt[i][j]) {
                        opt[i][j] = ten;
                        sol[i][j] = k;
                    }
                }
            }
        }

        Result result = new Result();
        result.cost = opt[1][N - 1];
        result.combination = buildCombination(N, sol);
        return result;
    }

    private static String buildCombination(int N, int[][] sol) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < N; i++) {
            builder.append((char) ('A' + (i - 1)));
        }
        return addBracket(builder.toString(), 1, N - 1, sol[1][N - 1], sol);
    }

    private static String addBracket(String str, int i, int j, int k, int[][] sol) {
        if (str.length() <= 2) return str;
        String a = str.substring(i - 1, k);
        String b = str.substring(k, j);
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append(addBracket(a, i, k, sol[i][k], sol));
        builder.append(")(");
        builder.append(addBracket(b, 1, j - k, sol[k][j], sol));
        builder.append(")");
        return builder.toString();
    }
}
