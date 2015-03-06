package com.interview.algorithms.general;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/15/14
 * Time: 2:34 PM
 */
public class C1_56_StationDistance {
    public int[] sumDistance;
    public int sum;

    public C1_56_StationDistance(int[] distance){
        this.sumDistance = new int[distance.length];
        int sum = 0;
        sumDistance[0] = 0;
        for(int i = 1; i < distance.length; i++){
            sum += distance[i];
            sumDistance[i] = sum;
        }
        this.sum = sum + distance[0];
    }

    private boolean validStation(int i){
        return (i >= 0 && i < sumDistance.length);
    }

    public int distance(int i, int j){
        if(!validStation(i) || !validStation(j)) return -1;
        int start = Math.min(i, j);
        int end = Math.max(i, j);
        int op = sumDistance[end] - sumDistance[start];
        return Math.min(op, this.sum - op);
    }
}
