package edu.princeton.cs.algs4.ch12;

import edu.princeton.cs.introcs.StdOut;
import edu.princeton.cs.introcs.StdRandom;

/**
 * Created by zqhxuyuan on 15-3-1.
 */
public class Flips {
    public static void main(String[] args) {
        int T = Integer.parseInt(args[0]);

        Counter heads = new Counter("heads");
        Counter tails = new Counter("tails");

        for(int t = 0; t < T; t++){
            if(StdRandom.bernoulli(0.5))
                heads.increment();
            tails.increment();
        }

        StdOut.println(heads);
        StdOut.println(tails);

        int d = heads.tally() - tails.tally();
        System.out.println("delta:" + Math.abs(d));
    }
}
