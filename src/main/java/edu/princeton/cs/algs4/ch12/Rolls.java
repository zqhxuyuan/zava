package edu.princeton.cs.algs4.ch12;

import edu.princeton.cs.introcs.StdOut;
import edu.princeton.cs.introcs.StdRandom;

/**
 * Created by zqhxuyuan on 15-3-1.
 */
public class Rolls {

    public static void main(String[] args) {
        //int T = Integer.parseInt(args[0]);
        int T = 100000;
        int SIDES = 6;

        Counter[] rolls = new Counter[SIDES+1];
        for (int i = 1; i <= SIDES; i++) {
            rolls[i] = new Counter(i + "'s");
        }

        for (int t = 0; t < T; t++) {
            int result = StdRandom.uniform(1, SIDES+1);
            rolls[result].increment();
        }

        for (int i = 1; i <= SIDES; i++) {
            StdOut.println(rolls[i]);
        }
    }
}
