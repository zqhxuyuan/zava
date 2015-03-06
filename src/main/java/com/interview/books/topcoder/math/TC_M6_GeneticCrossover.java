package com.interview.books.topcoder.math;

/**
 * Created_By: stefanie
 * Date: 15-1-18
 * Time: 下午5:34
 */
public class TC_M6_GeneticCrossover {
    int n;
    int[] d = new int[200];
    double[] power = new double[200];

    public double cross(String p1a, String p1b, String p2a, String p2b, int[] dom, int[] rec, int[] dependencies) {
        double fitness = 0.0;
        n = rec.length;
        for (int i = 0; i < n; i++) d[i] = dependencies[i];
        for (int i = 0; i < n; i++) power[i] = -1.0;
        for (int i = 0; i < n; i++)
            if (power[i] == -1.0) detchr(p1a, p1b, p2a, p2b, i);
        // we check if the dominant character of gene I has
        // not already been computed

        for (int i = 0; i < n; i++)
            fitness = fitness + power[i] * dom[i] + (1 - power[i]) * rec[i];
        // we compute the expected 'quality' of an animal based on the
        // probabilities of each gene to be expressed dominantly

        return fitness;
    }

    private double detchr(String p1a, String p1b, String p2a, String p2b, int nr) {
        double p, p1, p2;
        p = p1 = p2 = 1.0;
        if (p1a.charAt(nr) <= 'Z') p1 = p1 - 0.5; //  is a dominant gene
        if (p1b.charAt(nr) <= 'Z') p1 = p1 - 0.5;
        if (p2a.charAt(nr) <= 'Z') p2 = p2 - 0.5;
        if (p2b.charAt(nr) <= 'Z') p2 = p2 - 0.5;
        p = 1 - p1 * p2;
        if (d[nr] != -1) power[nr] = p * detchr(p1a, p1b, p2a, p2b, d[nr]);
        // gene 'nr' is dependent on gene d[nr]
        else power[nr] = p;

        return power[nr];
    }

    public static void main(String[] args){
        TC_M6_GeneticCrossover crossover = new TC_M6_GeneticCrossover();
        double cross = crossover.cross("AaaAA", "AaaAA", "AaaAA", "AaaAA", new int[]{1,2,3,4,5},
                new int[]{-1,-2,-3,-4,-5}, new int[]{-1,-1,-1,-1,1});
        System.out.println(cross);  //-5.0

        cross = crossover.cross("AbegG", "ABEgG", "aBEgg", "abegG", new int[]{5,5,5,5,5},
                new int[]{1,1,1,1,1}, new int[]{-1,0,1,2,3});
        System.out.println(cross);  //14.25
    }
}
