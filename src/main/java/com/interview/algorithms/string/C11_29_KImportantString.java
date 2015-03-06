package com.interview.algorithms.string;

/**
 * Created_By: stefanie
 * Date: 14-9-22
 * Time: 上午11:49
 * <p/>
 * You are given a set of N strings S0, S1, …, SN-1. These strings consist of only lower case characters a..z and have the same length L.
 * A string H is said to be K-important if there are at least K strings in the given set of N strings appearing at K different positions in H.
 * These K strings need not to be distinct.
 * Your task is to find the shortest K-important string. If there are more than one possible solution, your program can output any of them.
 *
 * using dynamic programming:
 *  common[i][j]: the common part of strs[i], strs[j], when strs[i] add after strs[j]
 *  opt[k][i]: select K str, and k-th str is strs[i], the min H length
 *      opt[k][i] = min{ opt[k-1][j] + unduplicate(strs[j],strs[i]) }
 *          unduplicate(strs[j],strs[i]) = L - common(strs[i], strs[j])
 *  sol[k][i]: save the previous of i
 *
 *  find the min length by search the minimal in opt[K], and backtrace in sol to get the result
 */
public class C11_29_KImportantString {
    public static String find(String[] strs, int L, int K){
        if(K == 1) return strs[0];

        int[][] common = new int[strs.length][strs.length];
        for(int i = 0; i < strs.length; i++){
            for(int j = 0; j < strs.length; j++){
                common[i][j] = common(strs[i], strs[j]);
            }
        }

        int[][] opt = new int[K + 1][strs.length];
        int[][] sol = new int[K + 1][strs.length];

        for(int i = 0; i < strs.length; i++) {
            opt[1][i] = L;
            sol[1][i] = -1;
        }

        for(int k = 2; k <= K; k++){
            for(int i = 0; i < strs.length; i++){
                opt[k][i] = Integer.MAX_VALUE;
                for(int j = 0; j < strs.length; j++){

                    int ten = opt[k-1][j] + L - common[i][j];
                    if(ten < opt[k][i]){
                        opt[k][i] = ten;
                        sol[k][i] = j;
                    }
                }
            }
        }

        int min = Integer.MAX_VALUE;
        int last = -1;
        for(int i = 0; i < strs.length; i++){
            if(opt[K][i] < min){
                min = opt[K][i];
                last = i;
            }
        }

        StringBuilder builder = new StringBuilder();
        int next = last;
        builder.append(strs[next]);
        while(K > 1){
            last = sol[K--][last];
            int c = common[next][last];
            builder.insert(0, strs[last].substring(0, L - c));
            next = last;
        }

        return builder.toString();
    }

    public static int common(String a, String b){
        int i = a.length() - 1;
        for(; i >= 1; i--){
            String prefix = a.substring(0, i);
            if(b.endsWith(prefix)) break;
        }
        return i;
    }
}
