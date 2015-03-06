package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-8-26
 * Time: 上午9:17
 */
public class C4_49_ClosewiselyPrintMatrix {

    public static void print(int[][] a){
        int N = a.length;
        int M = a[0].length;
        int si = 0;
        int sj = 0;
        while(N > si && M > sj){
            int i = si;
            int j = sj - 1;
            while(j < M - 1)    System.out.print(a[i][++j] + " ");
            while(i < N - 1)    System.out.print(a[++i][j] + " ");
            while(j > sj)       System.out.print(a[i][--j] + " ");
            while(i > si + 1)   System.out.print(a[--i][j] + " ");
            if(si < N)  si++; N--;
            if(sj < M)  sj++; M--;
        }
        System.out.println();
    }
}
