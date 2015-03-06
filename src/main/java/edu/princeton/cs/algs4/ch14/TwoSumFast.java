package edu.princeton.cs.algs4.ch14;

import edu.princeton.cs.algs4.ch11.BinarySearch;

import java.util.Arrays;

/**
 * Created by zqhxuyuan on 15-3-1.
 */
public class TwoSumFast {

    public static void main(String[] args) {

    }

    public static int count(int[] a){
        Arrays.sort(a);
        int N = a.length;
        int count = 0;

        for (int i = 0; i < N; i++) {
            if(BinarySearch.rank(-a[i], a) > i)
                count ++;
        }

        return count;
    }
}
