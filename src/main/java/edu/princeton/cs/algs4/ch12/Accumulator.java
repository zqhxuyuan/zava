package edu.princeton.cs.algs4.ch12;

import edu.princeton.cs.introcs.StdOut;
import edu.princeton.cs.introcs.StdRandom;

/**
 * Created by zqhxuyuan on 15-3-1.
 */
public class Accumulator {

    private double total;
    private int N;

    public void addDataValue(double val){
        N++;
        total += val;
    }

    public double mean(){
        return total / N;
    }

    @Override
    public String toString() {
        return "Accumulator:" + total + "/" + N;
    }

    public static void main(String[] args) {
        int T = 1000;
        Accumulator a = new Accumulator();
        for (int i = 0; i < T; i++) {
            a.addDataValue(StdRandom.random());
        }
        StdOut.println(a);
    }
}
