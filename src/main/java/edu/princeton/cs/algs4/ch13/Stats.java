package edu.princeton.cs.algs4.ch13;

import edu.princeton.cs.introcs.StdIn;
import edu.princeton.cs.introcs.StdOut;

/**
 * Created by zqhxuyuan on 15-3-1.
 */
public class Stats {

    public static void main(String[] args) {
        Bag<Double> numbers = new Bag<>();

        while(!StdIn.isEmpty()){
            numbers.add(StdIn.readDouble());
        }
        int N = numbers.size();

        double sum = 0.0;
        for (double x : numbers)
            sum += x;
        double mean = sum / N;

        StdOut.println("Mean:" + mean);
    }
}
