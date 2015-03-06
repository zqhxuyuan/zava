package com.interview.algorithms.design;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/30/14
 * Time: 12:18 PM
 *
 * You have a stream of infinite queries (ie: real time Google search queries that people are entering).
 * Describe how you would go about finding a good estimate of 1000 samples from this never ending set of data and then write code for it.
 *
 * Idea: keep total number count N. If N<=m, just keep it.
 * For N>m, generate a random number R=rand(N) in [0, N), replace a[R] with new number if R falls in [0, m).
 */
public class C10_6_SampleForInfiniteSet {

    public static List<String> simpling(String filePath, int N){
        List<String> queries = new ArrayList<>();
        try {
            FileInputStream f = new FileInputStream(filePath);
            BufferedReader dr = new BufferedReader(new InputStreamReader(f));
            String line = dr.readLine();
            int count = 0;
            while (line != null) {
                count++;
                if(count <= N) queries.add(line.trim());
                else {
                    int r = new Random().nextInt(count);
                    if(r < N) queries.set(r, line.trim());
                }
                line = dr.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queries;
    }
}
