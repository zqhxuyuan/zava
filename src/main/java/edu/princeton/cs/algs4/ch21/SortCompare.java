package edu.princeton.cs.algs4.ch21;

import edu.princeton.cs.algs4.ch14.Stopwatch;
import edu.princeton.cs.algs4.ch24.Heap;
import edu.princeton.cs.introcs.StdRandom;

/**
 * Created by zqhxuyuan on 15-3-1.
 *
 * P161.zh.4E
 */
public class SortCompare {

    public static double time(String alg, Double[] a){
        Stopwatch timer = new Stopwatch();
        if(alg.equals("Insertion")) Insertion.sort(a);
        else if(alg.equals("Selection")) Selection.sort(a);
        else if(alg.equals("Shell")) Shell.sort(a);
        else if(alg.equals("Merge")) Merge.sort(a);
        else if(alg.equals("Quick")) Quick.sort(a);
        else if(alg.equals("Heap")) Heap.sort(a);
        return timer.elapsedTime();
    }

    public static double timeRandomInput(String alg, int N, int T){
        double total = 0.0;
        Double[] a = new Double[N];
        for (int t = 0; t < T; t++) {
            for (int i = 0; i < N; i++) {
                a[i] = StdRandom.uniform();
            }
            total += time(alg, a);
        }
        return total;
    }

    public static void main(String[] args) {
        String alg1 = args[0];
        String alg2 = args[1];
        int N = Integer.parseInt(args[2]);
        int T = Integer.parseInt(args[3]);
        double t1 = timeRandomInput(alg1, N, T);
        double t2 = timeRandomInput(alg2, N, T);
    }
}
