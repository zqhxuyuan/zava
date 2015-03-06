package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-7-22
 * Time: 下午7:56
 */
public class C4_31_CombinationFinder {

    public static void find(int N, int M){
        if(N > M) find(M, M);
        int[] aux = new int[N];
        find(M, 0, aux, N);
    }

    private static void find(int dest, int idx, int[] aux, int N){
        if(dest == 0) dump(aux);
        if(dest <= 0 || idx == N) return;
        find(dest, idx+1, aux, N);
        aux[idx] = 1;
        find(dest-idx-1, idx+1, aux, N);
        aux[idx] = 0;
    }

    private static void dump(int[] aux){
        for(int i = 0; i < aux.length; i++){
            if(aux[i] == 1) System.out.print(i+1 + "\t");
        }
        System.out.println();
    }
}
